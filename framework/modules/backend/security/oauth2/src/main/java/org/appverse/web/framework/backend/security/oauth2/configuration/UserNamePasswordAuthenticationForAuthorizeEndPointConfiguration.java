package org.appverse.web.framework.backend.security.oauth2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.appverse.web.framework.backend.security.oauth2.login.CustomUserNamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@ConditionalOnProperty(value="appverse.frontfacade.oauth2.apiprotection.enabled", matchIfMissing=false)
public class UserNamePasswordAuthenticationForAuthorizeEndPointConfiguration extends WebSecurityConfigurerAdapter {
	
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable();

    	CustomUserNamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter = new CustomUserNamePasswordAuthenticationFilter(authenticationManager);
    	customUsernamePasswordAuthenticationFilter.setUserNamePasswordAuthenticationUri("/oauth/authorize");
    	http.addFilterBefore(customUsernamePasswordAuthenticationFilter, BasicAuthenticationFilter.class);    	
    }
}