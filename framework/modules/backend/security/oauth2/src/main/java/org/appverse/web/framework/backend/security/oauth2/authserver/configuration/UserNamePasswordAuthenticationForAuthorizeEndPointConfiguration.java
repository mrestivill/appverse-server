package org.appverse.web.framework.backend.security.oauth2.authserver.configuration;

import org.appverse.web.framework.backend.security.authentication.userpassword.filters.CustomUserNamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


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
    
    // TODO: Move this to its own class...
	@Bean
	public DefaultTokenServices tokenServices(TokenStore tokenStore){
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setTokenStore(tokenStore);
		// We enable support refresh token as default is false.
		// This is possible because refresh token can be enabled / disabled by specifying the 
		// "refresh_token" grant type in the clients registration in the authoriztion server.
		// Is this grant is not specified no refresh token is issued.
		tokenServices.setSupportRefreshToken(true);
		return tokenServices;
	}    
}