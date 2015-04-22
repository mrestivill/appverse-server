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

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Front Facade module when Jersey is present.
 */
@Configuration
@ConditionalOnClass(name = {
		"org.glassfish.jersey.server.spring.SpringComponentProvider",
		"javax.servlet.ServletRegistration" })
@ConditionalOnBean(type = "org.glassfish.jersey.server.ResourceConfig")
@EnableConfigurationProperties(FrontFacadeAutoconfigurationProperties.class)
public class FrontFacadeRestJerseyAutoConfiguration extends ResourceConfig {
	
	// This is the reason why frontServiceProperties are null
	// http://stackoverflow.com/questions/25764459/spring-boot-application-properties-value-not-populating
	
	@Autowired 
	FrontFacadeAutoconfigurationProperties frontFacadeAutoconfigurationProperties;

	public FrontFacadeRestJerseyAutoConfiguration() {
	}
	
	@PostConstruct
	public void init() {
		// 	Create a recursive package scanner
		PackageNamesScanner resourceFinder = new PackageNamesScanner(new String[]{"org.appverse.web.framework.backend.frontfacade.rest"}, true);
		// Register the scanner with this Application and JacksonFeature
		registerFinder(resourceFinder);
		register(JacksonFeature.class);
	}

}
