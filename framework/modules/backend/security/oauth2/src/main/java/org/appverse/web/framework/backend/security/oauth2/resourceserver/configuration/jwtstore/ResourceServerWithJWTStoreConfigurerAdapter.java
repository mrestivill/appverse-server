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
package org.appverse.web.framework.backend.security.oauth2.resourceserver.configuration.jwtstore;

import java.security.KeyPair;

import org.appverse.web.framework.backend.security.oauth2.resourceserver.handlers.OAuth2LogoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * Convenient setup for an OAuth2 Resource Server that uses a
 * JwtTokenStore.
 *  
 * Take into account that JwtTokenStore is not a proper store in the regard of keeping the tokens somewhere.
 * The reason is that the JwtTokens have all the necessary user information themselves (they are opaque to clients) and so
 * it is not necessary a physical store (such as JdbcTokenStore).
 * While this can be an advantage, take into account that the tokens will be larger as they contain more information and besides 
 * it is not possible to revoke JwtTokens as they are not physically stored.
 * You need to take into account that you need a certificate to cypher the token (for more info you can read about how
 * JWT works). 
 * The resource server will need to use exactly the same implementation of the JwtAccessTokenConverter to decode the token.
 */
public class ResourceServerWithJWTStoreConfigurerAdapter extends ResourceServerConfigurerAdapter {

    @Value("${appverse.frontfacade.rest.api.basepath:/api}")
    protected String apiPath;
	@Value("${appverse.frontfacade.oauth2.logoutEndpoint.path:/sec/logout}")
	protected String oauth2LogoutEndpointPath;
	@Value("${appverse.frontfacade.swagger.enabled:true}")
	protected boolean swagerEnabled;
	@Value("${appverse.frontfacade.swagger.oauth2.allowedUrls.antMatchers:/webjars/springfox-swagger-ui/**,/configuration/security,/configuration/ui,/swagger-resources,/api-docs/**,/v2/api-docs/**,/swagger-ui.html/**,/swaggeroauth2login,/o2c.html,/}")
	protected String swaggerOauth2AllowedUrlsAntMatchers;
	
	@Bean
	public OAuth2LogoutHandler oauth2LogoutHandler() {
		return new OAuth2LogoutHandler(tokenStore());
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources)
			throws Exception {
		resources.tokenStore(tokenStore());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// In this OAuth2 scenario with implicit flow we both login the user and obtain the token
		// in the same endpoint (/oauth/authorize). User credentials will be passed as "username" and 
		// "password" form. 
		// This might be different in other scenarios, for instance if we wanted to implement
		// authorization code flow to support token refresh.
		http.
			httpBasic().disable()
		// Test filter gives problems because is redirecting to / is not saving the request to redirect properly
		.logout()
        	.logoutUrl(apiPath + oauth2LogoutEndpointPath)
        	.logoutSuccessHandler(oauth2LogoutHandler());

		if (swagerEnabled){
		// If swagger is enabled we need to permit certain URLs and resources for Swagger UI to work with OAuth2
		http
        	.authorizeRequests().antMatchers(swaggerOauth2AllowedUrlsAntMatchers.split(",")).permitAll();
		}
		http.authorizeRequests().anyRequest().authenticated();
	}
	
	@Bean
	public JwtAccessTokenConverter jwtTokenEnhancer() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		KeyPair keyPair = new KeyStoreKeyFactory(
				new ClassPathResource("keystore.jks"), "foobar".toCharArray())
				.getKeyPair("test");
		converter.setKeyPair(keyPair);
		return converter;
	}

	@Bean
	protected TokenStore tokenStore() {
		return new JwtTokenStore(jwtTokenEnhancer());
	}	
	
	@Bean
    public ResourceServerTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenEnhancer(jwtTokenEnhancer());
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }	
}
