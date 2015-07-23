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
package org.appverse.web.framework.backend.security.oauth2.configuration.jdbcstore;

import org.appverse.web.framework.backend.security.oauth2.handlers.OAuth2LogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * Convenient setup for an OAuth2 Resource Server that uses a
 * JdbcTokenStore to keep the tokens. This way you only need to override
 * configure method if you want to change default HttpSecurity (by default requiring 
 * authentication for any request)
 * 
 * Take into account that ClientDetailsStore, TokenStore, AuthorizationCodeStore
 * etc, could be in-memory instead of be persisted in database if you had just a
 * node for the AuthorizationServer (for instance). You can use the setup
 * provided by this class but remember that you need to assess your scenario
 * taking into account how you are going to scale your Authorization and
 * Resource server, performance requirements, etc. Depending on this you will be
 * able to use in-memory or not and you will need to think if you require sticky
 * sessions (for Authorization server if you are using stateful grant types) or
 * need to use another solution as Spring Session in combination with this
 * setup. Take this setup just as an starting point. 
 * 
 * JDBCTokenStore requires a particular database model provided by Spring as an SQL script
 * that you need to create in your database schema.
 * You can find a schema example here:
 * https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/test/resources/schema.sql 
 */
public class ResourceServerWithJDBCStoreConfigurerAdapter extends ResourceServerConfigurerAdapter {

	@Autowired
	private TokenStore tokenStore;
	
	@Value("${appverse.frontfacade.oauth2.logoutEndpoint.path:/sec/logout}")
	protected String oauth2LogoutEndpointPath;
	
	@Bean
	public OAuth2LogoutHandler oauth2LogoutHandler() {
		return new OAuth2LogoutHandler(tokenStore);
	}

	/* Necessary for swagger so that once we redirect after the login we can go to o2c.html */
	// http://stackoverflow.com/questions/26833452/spring-boot-redirect-to-current-page-after-succes-login
	@Bean
	public AuthenticationSuccessHandler successHandler() {
	    SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
	    handler.setDefaultTargetUrl("/o2c.html");
	    // handler.setUseReferer(true);
	    return handler;
	}
	
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources)
			throws Exception {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.logout()
        	.logoutUrl(oauth2LogoutEndpointPath)
        	.logoutSuccessHandler(oauth2LogoutHandler())
        .and()       
     	// TODO: This should be conditioned to swagger enabled
        /* Swagger integration - work in progress
            .formLogin()
            	//.loginProcessingUrl("/sec/login")
            	// .loginProcessingUrl("/oauth/token")
                // .failureUrl("/login.jsp?authentication_error=true")
                //.loginPage("/login.jsp")
            	.loginPage("/oauth2loginform.html")
            	.successHandler(successHandler())
        .and()
        	.authorizeRequests().antMatchers("/oauth2loginform.html").permitAll().and()
        	.authorizeRequests().antMatchers("/o2c.html").permitAll().and()
        	.authorizeRequests().antMatchers("/").permitAll().and()
        	.authorizeRequests().antMatchers("/index.html").permitAll().and()
        	.authorizeRequests().antMatchers("/css/**").permitAll().and()
        	.authorizeRequests().antMatchers("/lib/**").permitAll().and()
        	.authorizeRequests().antMatchers("/swagger-ui.js").permitAll().and()        	
        	.authorizeRequests().antMatchers("/api-docs/**").permitAll().and()
        */
    // TODO: This should be conditioned to swagger enabled
			.authorizeRequests().anyRequest().authenticated();
	}

}
