package org.appverse.web.framework.backend.security.oauth2.authserver.token.enhancers;

import java.util.HashMap;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * Token enhancer implementation that adds the user list of authorities to the return.
 */
public class PrincipalCredentialsTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);

		HashMap<String, Object> additionalInfoMap = new HashMap<String, Object>();
        // Here we would need to format the info we want to return, just the authorities by now
        additionalInfoMap.put("authorities", authentication.getAuthorities());
        result.setAdditionalInformation(additionalInfoMap);
		return result;
	}

}
