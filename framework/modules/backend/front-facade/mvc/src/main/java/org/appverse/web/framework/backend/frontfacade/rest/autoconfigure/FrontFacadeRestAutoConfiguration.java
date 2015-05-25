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

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Front Facade module when Jersey is present.
 */
@Configuration
public class FrontFacadeRestAutoConfiguration /*extends ResourceConfig*/ {
	
	public FrontFacadeRestAutoConfiguration() {
	}
	
	/*
	 * Init method requires to be annotated with {@link PostConstruct} as we need properties to be injected 
	 */
	@PostConstruct
	public void init() {
		/* TODO: We need a different solution for this now (to enable - disable)
		// Register the modules endpoints if enabled and JacksonFeature	
		if (frontFacadeRestAutoconfigurationProperties.isRemoteLogEndpointEnabled()){
			register(RemoteLogServiceFacadeImpl.class);
		}
		if (frontFacadeRestAutoconfigurationProperties.isBasicAuthenticationEndpointEnabled()){
			register(BasicAuthenticationServiceImpl.class);			
		}
		if( frontFacadeRestAutoconfigurationProperties.isJerseyExceptionHandlerEnabled()) {
			register(JerseyExceptionHandler.class);
		}
		register(JacksonFeature.class);
		*/
	}

}
