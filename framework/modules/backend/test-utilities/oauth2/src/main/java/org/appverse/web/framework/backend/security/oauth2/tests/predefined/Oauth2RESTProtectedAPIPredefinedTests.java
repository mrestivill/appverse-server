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
package org.appverse.web.framework.backend.security.oauth2.tests.predefined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.security.oauth2.tests.common.AbstractIntegrationTests;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.client.RestTemplate;


/**
 * Necessary to have httpclient for this tests (with scope tests) so that the http returns are handle correctly.
 * Otherwise you will experience exactly the problem described here:
 * http://stackoverflow.com/questions/27341604/exception-when-using-testresttemplate 
 */
public class Oauth2RESTProtectedAPIPredefinedTests extends AbstractIntegrationTests {
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	private String baseApiPath;
	
	@Value("${appverse.frontfacade.rest.remoteLogEndpoint.path:/remotelog/log}")
	private String remoteLogEndpointPath;
	
	@Autowired
    private FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private EmbeddedWebApplicationContext server;	
		
	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private ClientDetailsService clientDetailsService;
	
	RestTemplate restTemplate = new TestRestTemplate();
	
	protected String getPassword() {
		return "password";
	}

	protected String getUsername() {
		return "user";
	}	

	@Test
	public void contextLoads() {
		assertTrue("Wrong token store type: " + tokenStore, tokenStore instanceof JdbcTokenStore);
		assertTrue("Wrong client details type: " + clientDetailsService, JdbcClientDetailsService.class.isAssignableFrom(AopUtils.getTargetClass(clientDetailsService)));
	}
		
	@Test
	@OAuth2ContextConfiguration(resource = NonAutoApproveImplicit.class, initialize = false)
	public void testPostForNonAutomaticApprovalToken() throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", getBasicAuthentication());
		context.getAccessTokenRequest().setHeaders(headers);
		try {
			assertNotNull(context.getAccessToken());
			fail("Expected UserRedirectRequiredException");
		}
		catch (UserRedirectRequiredException e) {
			// ignore
		}
		// add user approval parameter for the second request
		context.getAccessTokenRequest().add(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
		context.getAccessTokenRequest().add("scope.read", "true");
		assertNotNull(context.getAccessToken());
	}

	static class NonAutoApproveImplicit extends ImplicitResourceDetails {
		public NonAutoApproveImplicit(Object target) {
			super();
			setClientId("test-client");
			setId(getClientId());
			setPreEstablishedRedirectUri("http://anywhere");
		}
	}
	
	@Test
	public void testProtectedResourceIsProtected() throws Exception {
		ResponseEntity<String> response = http.getForString("/");
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertTrue("Wrong header: " + response.getHeaders(), response.getHeaders()
				.getFirst("WWW-Authenticate").startsWith("Bearer realm="));
	}
		
	@Test
	@OAuth2ContextConfiguration(resource = NonAutoApproveImplicit.class, initialize = false)
	public void testProtectedRemoteLogWithToken() throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", getBasicAuthentication());
		context.getAccessTokenRequest().setHeaders(headers);
		try {
			assertNotNull(context.getAccessToken());
			fail("Expected UserRedirectRequiredException");
		}
		catch (UserRedirectRequiredException e) {
			// ignore
		}
		// add user approval parameter for the second request
		context.getAccessTokenRequest().add(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
		context.getAccessTokenRequest().add("scope.read", "true");
		assertNotNull(context.getAccessToken());
		
		// OAuth2RestTemplate template = new OAuth2RestTemplate(resource, new DefaultOAuth2ClientContext(context.getAccessToken()));
        RemoteLogRequestVO remoteLogRequest = new RemoteLogRequestVO();
        remoteLogRequest.setLogLevel("DEBUG");
        remoteLogRequest.setMessage("This is my log message!");
        
        int port = server.getEmbeddedServletContainer().getPort();
        
        ResponseEntity<String> result2 = http.getRestTemplate().postForEntity("http://localhost:" + port + baseApiPath + remoteLogEndpointPath, remoteLogRequest, String.class);
        assertEquals(HttpStatus.OK, result2.getStatusCode());
	}
	
	@Test
	public void testRemoteLogIsProtected() throws Exception {
        RemoteLogRequestVO remoteLogRequest = new RemoteLogRequestVO();
        remoteLogRequest.setLogLevel("DEBUG");
        remoteLogRequest.setMessage("This is my log message!");
        
        int port = server.getEmbeddedServletContainer().getPort();
		
		ResponseEntity<String> response = http.getRestTemplate().postForEntity("http://localhost:" + port + baseApiPath + remoteLogEndpointPath, remoteLogRequest, String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertTrue("Wrong header: " + response.getHeaders(), response.getHeaders()
				.getFirst("WWW-Authenticate").startsWith("Bearer realm="));
	}
	
}
