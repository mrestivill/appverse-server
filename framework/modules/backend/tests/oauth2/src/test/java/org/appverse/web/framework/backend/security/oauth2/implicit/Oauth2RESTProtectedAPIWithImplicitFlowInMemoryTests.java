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
package org.appverse.web.framework.backend.security.oauth2.implicit;

import org.appverse.web.framework.backend.security.oauth2.ApplicationInMemory;
import org.appverse.web.framework.backend.security.oauth2.ApplicationJDBC;
import org.appverse.web.framework.backend.test.util.oauth2.tests.predefined.implicit.Oauth2ImplicitFlowPredefinedTests;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;

/**
 * Triggers OAuth2 predefined tests passing username, password and client id to use.
 * 
 * Necessary to have httpclient for this tests (with scope tests) so that the http returns are handle correctly.
 * Otherwise you will experience exactly the problem described here:
 * http://stackoverflow.com/questions/27341604/exception-when-using-testresttemplate 
 */
@SpringApplicationConfiguration(classes = ApplicationInMemory.class)
@IntegrationTest(value={"server.port=0",		         
		         "appverse.frontfacade.oauth2.apiprotection.enabled=true",
		         "appverse.frontfacade.rest.http.basic.default.setup.enabled=false",
		         "appverse.frontfacade.rest.basicAuthenticationEndpoint.enabled=false",
		         "appverse.frontfacade.rest.simpleAuthenticationEndpoint.enabled=false"})
public class Oauth2RESTProtectedAPIWithImplicitFlowInMemoryTests extends Oauth2ImplicitFlowPredefinedTests {

	@Override
	protected String getPassword() {
		return "password";
	}

	@Override
	protected String getUsername() {
		return "user";
	}

	@Override
	protected String getClientId() {
		return "test-client-autoapprove";
	}

}
