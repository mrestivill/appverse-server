package org.appverse.web.framework.backend.frontfacade.rest.authentication.basic.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public abstract class AppverseBasicAuthenticationConfigurerAdapter extends WebSecurityConfigurerAdapter {
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	protected String baseApiPath;
	
	@Value("${appverse.frontfacade.rest.basicAuthenticationEndpoint.path:/sec/login}")
	protected String basicAuthenticationEndpointPath;

	@Value("${appverse.frontfacade.rest.simpleAuthenticationEndpoint.path:/sec/simplelogin}")
	protected String simpleAuthenticationEndpointPath;
			
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http
        .csrf()
        	.disable()
        .authorizeRequests()
        	.antMatchers(baseApiPath + basicAuthenticationEndpointPath).permitAll()
        	.antMatchers(baseApiPath + simpleAuthenticationEndpointPath).permitAll()
            .antMatchers(baseApiPath + "/**").fullyAuthenticated()
            .antMatchers("/").permitAll()
            .and()
        .logout()
            .permitAll().and()
        .httpBasic();
	}
}	