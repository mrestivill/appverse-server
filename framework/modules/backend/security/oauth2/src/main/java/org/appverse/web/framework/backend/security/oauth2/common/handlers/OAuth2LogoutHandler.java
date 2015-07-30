/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0”). If a copy of the APL was not distributed with this 
 file, You can obtain one at http://www.appverse.mobi/licenses/apl_v2.0.pdf. [^]

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the conditions of the AppVerse Public License v2.0 
 are met.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. EXCEPT IN CASE OF WILLFUL MISCONDUCT OR GROSS NEGLIGENCE, IN NO EVENT
 SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) 
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.
 */
package org.appverse.web.framework.backend.security.oauth2.common.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.Assert;

/**
 * This logout handler will just invalidate the OAuth2 token that was granted in the
 * authentication endpoint when the user wants to logout.
 * The concept of 'login' and 'logout' with OAuth2 is a bit 'weird' or little natural
 * as most of the times OAuth2 is used in stateless applications (which is one of its
 * biggest advantages). So in this case we just remove the token from the token store
 * and we return HTTP OK.
 */
public class OAuth2LogoutHandler extends
		AbstractAuthenticationTargetUrlRequestHandler implements
		LogoutSuccessHandler {

	protected TokenStore tokenStore;
	
	public OAuth2LogoutHandler(TokenStore tokenStore){
		Assert.state(tokenStore != null, "TokenStore cannot be null in OAuth2LogoutHandler");
		this.tokenStore = tokenStore;
	}
	
	public OAuth2LogoutHandler tokenStore(TokenStore tokenStore) {
		Assert.state(tokenStore != null, "TokenStore cannot be null in OAuth2LogoutHandler");
		this.tokenStore = tokenStore;
		return this;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		// We get the token and then we remove it from the tokenStore
		// We have to take into account that the OAuth2 spec allows the access token to be passed
		// in the authorization header or as a parameter
		String authorizationHeader = request.getHeader("authorization");
		String accessToken=null;

		if (authorizationHeader != null) {
			String authorizationType = authorizationHeader.substring(0,
					OAuth2AccessToken.BEARER_TYPE.length());
			if (authorizationType
					.equalsIgnoreCase(OAuth2AccessToken.BEARER_TYPE)) {
				
				accessToken = authorizationHeader.substring(
						OAuth2AccessToken.BEARER_TYPE.length()).trim();				
			}
		}
		else{
			accessToken = request.getParameter("access_token");
		}

		if (accessToken != null){
			final OAuth2AccessToken oAuth2AccessToken = tokenStore
					.readAccessToken(accessToken);

			if (oAuth2AccessToken != null) {
				tokenStore.removeAccessToken(oAuth2AccessToken);
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
