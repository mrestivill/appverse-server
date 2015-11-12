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
package org.appverse.web.framework.backend.security.oauth2.authserver.configuration.jwtstore;

import java.security.KeyPair;

import javax.sql.DataSource;

import org.appverse.web.framework.backend.security.oauth2.authserver.token.enhancers.PrincipalCredentialsTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * Convinient setup for an OAuth2 Authorization Server that uses a
 * JwtTokenStore. This way you only need to override
 * configure method to register the clients.
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
public class AuthorizationServerWithJWTStoreConfigurerAdapter extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager auth;
		
		@Autowired
		protected DataSource dataSource;		

		@Value("${appverse.frontfacade.oauth2.tokenEndpoint.path:/oauth/token}")
		protected String oauth2TokenEndpointPath;
		
		@Value("${appverse.frontfacade.oauth2.authorizeEndpoint.path:/oauth/authorize}")
		protected String oauth2AuthorizeEndpointPath;

		protected BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		@Bean
		public JwtAccessTokenConverter jwtTokenEnhancer() {
			// Implementation based on Java TrustStore. If you need something different you can
			// override and implement your own JwtAccessTokenConverter
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			KeyPair keyPair = new KeyStoreKeyFactory(
					new ClassPathResource("oauth-example-keystore.jks"), "guessThis".toCharArray())
					.getKeyPair("appverse-oauth-server-showcase");
			converter.setKeyPair(keyPair);
			return converter;
		}
		
		@Bean
		protected TokenStore tokenStore() {
			return new JwtTokenStore(jwtTokenEnhancer());
		}
		
		@Bean
		protected PrincipalCredentialsTokenEnhancer principalCredentialsTokenEnhancer(){
			return new PrincipalCredentialsTokenEnhancer();
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security)
				throws Exception {
			security.passwordEncoder(passwordEncoder);
			security.tokenKeyAccess("permitAll()").checkTokenAccess(
					"isAuthenticated()");
		}
		
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			endpoints.pathMapping("/oauth/token", oauth2TokenEndpointPath);			
			endpoints.pathMapping("/oauth/authorize", oauth2AuthorizeEndpointPath);
			
			endpoints.tokenStore(tokenStore())
					 // TODO: We can't use this or the JWT token is not generated... .tokenEnhancer(principalCredentialsTokenEnhancer())
					 .tokenEnhancer(jwtTokenEnhancer())
					 .authenticationManager(auth)
					 .approvalStoreDisabled();
		}
	}
