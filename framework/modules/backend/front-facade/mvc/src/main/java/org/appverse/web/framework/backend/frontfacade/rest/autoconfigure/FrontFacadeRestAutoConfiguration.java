/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.appverse.web.framework.backend.frontfacade.rest.autoconfigure;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Front Facade module when Jersey is present.
 */
@Configuration
@ConditionalOnClass(FrontFacadeRestAutoConfiguration.class)
@ComponentScan("org.appverse.web.framework.backend.frontfacade.rest")
public class FrontFacadeRestAutoConfiguration {
	
	public FrontFacadeRestAutoConfiguration() {
	}
	
	/*
	 * Init method requires to be annotated with {@link PostConstruct} as we need properties to be injected 
	 */
	@PostConstruct
	public void init() {
	}
	
	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	@ConditionalOnProperty(value="appverse.frontfacade.rest.basicAuthentication.enabled", matchIfMissing=true)
	protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {
		
		@Value("${appverse.frontfacade.rest.api.basepath:/api}")
		private String baseApiPath;
		
		@Value("${appverse.frontfacade.rest.basicAuthenticationEndpoint.path:/sec/login}")
		private String basicAuthenticationEndpointPath;

		@Value("${appverse.frontfacade.rest.simpleAuthenticationEndpoint.path:/sec/simplelogin}")
		private String simpleAuthenticationEndpointPath;
				
		@Autowired
		private SecurityProperties security;
		
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
}
