package org.appverse.web.framework.backend.security.oauth2.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
// import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LogoutHandler extends
		AbstractAuthenticationTargetUrlRequestHandler implements
		LogoutSuccessHandler {

	@Autowired
	private TokenStore tokenStore;

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		// We get the token and then we remove it from the tokenStore
		String token = request.getHeader("authorization");

		if (token != null) {
			String authorizationType = token.substring(0,
					OAuth2AccessToken.BEARER_TYPE.length());
			if (authorizationType
					.equalsIgnoreCase(OAuth2AccessToken.BEARER_TYPE)) {
				final OAuth2AccessToken oAuth2AccessToken = tokenStore
						.readAccessToken(token.substring(
								OAuth2AccessToken.BEARER_TYPE.length()).trim());

				if (oAuth2AccessToken != null) {
					tokenStore.removeAccessToken(oAuth2AccessToken);
				}
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
