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
package org.appverse.web.framework.backend.security.oauth2.login;

import org.appverse.web.framework.backend.security.authentication.userpassword.managers.UserAndPasswordAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@RestController
@ConditionalOnProperty(value="appverse.frontfacade.oauth2.apiprotection.enabled", matchIfMissing=true)
@RequestMapping(value = "${appverse.frontfacade.rest.api.basepath:/api}", method = RequestMethod.POST)
/**
 * OAuth2LoginService implementation that shows an enpoint to authenticate the user with username and password based form
 * authentitication. Once the user is authenticated the request is forwarded to the /oauth/authorize endpoint in order to 
 * obtain a token and the response is returned to the client.
 * This way this OAuth2 "login" endpoint can be used by clients to authenticate the user and to obtain the token in a 
 * single request. 
 * This is valid for scenarios with implicit grant flow.
 * The request is exactly the one required to obtain a token in the implicit flow adding the "username" and "password" 
 * parameters. This way all the required paramters in order to obtain a token (request type, redirect uri, client id and so on)
 * can be forwarded to the /oauth/authorize endpoint.
 */


/*
 * Replacing oauth2 endpoints with Java configuration results very complicated (with XML) is easy.
 * See: http://stackoverflow.com/questions/22222966/how-to-change-spring-security-oauth2-default-token-endpoint
 * That's the reason why we don't change token endpoint default URL for now.
 */
public class OAuth2LoginServiceImpl implements OAuth2LoginService {

	// private static final String CSRF_TOKEN_SESSION_ATTRIBUTE = "org.springframework.security.web.csrf.CsrfToken";

	@Value("${security.enable-csrf:true}")
	private boolean securityEnableCsrf;

	@Autowired
	private UserAndPasswordAuthenticationManager userAndPasswordAuthenticationManager;

	@Autowired(required = false)
	private ConsumerTokenServices tokenServices;

	/**
	 * Authenticates an user using oauth implicit grant flow. Requires basic authentication header.
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @return
	 * @throws Exception
	 */
	
	
	@RequestMapping(value = "${appverse.frontfacade.oauth2.logoutEndpoint.path:/sec/token/revoke}", method = RequestMethod.POST)
	@ConditionalOnExpression("#{tokenServices}!=null")
	public @ResponseBody void revokeToken(@RequestParam("access_token") String token, HttpServletRequest req) throws  InvalidClientException {
		if (tokenServices != null) {
			tokenServices.revokeToken(token);
		}else{
			throw new RuntimeException("There is not a ConsumerTokenServices available");
		}
	}

	@Override
	public void login(String username, String password, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {
		// TODO Auto-generated method stub
		
	}
}