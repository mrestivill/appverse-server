/*
 * Copyright 2012-2014 the original author or authors.
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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties for configuring {@link FrontFacadeRestAutoConfiguration}.
 */
@ConfigurationProperties(prefix = "appverse.frontfacade.rest", ignoreUnknownFields = false)
@Component
public class FrontFacadeRestAutoconfigurationProperties {

	private boolean remoteLogEndpointEnabled = true;
	private boolean basicAuthenticationEndpointEnabled = true;

	public boolean isBasicAuthenticationEndpointEnabled() {
		return basicAuthenticationEndpointEnabled;
	}

	public void setBasicAuthenticationEndpointEnabled(
			boolean basicAuthenticationEndpointEnabled) {
		this.basicAuthenticationEndpointEnabled = basicAuthenticationEndpointEnabled;
	}

	public boolean isRemoteLogEndpointEnabled() {
		return remoteLogEndpointEnabled;
	}

	public void setRemoteLogEndpointEnabled(boolean remoteLogEndpointEnabled) {
		this.remoteLogEndpointEnabled = remoteLogEndpointEnabled;
	}
}
