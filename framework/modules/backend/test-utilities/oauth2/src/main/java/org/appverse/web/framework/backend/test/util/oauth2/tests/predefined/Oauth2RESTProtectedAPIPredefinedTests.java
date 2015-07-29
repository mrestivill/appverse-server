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
package org.appverse.web.framework.backend.test.util.oauth2.tests.predefined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.test.util.oauth2.tests.common.AbstractIntegrationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * Necessary to have httpclient for this tests (with scope tests) so that the http returns are handle correctly.
 * Otherwise you will experience exactly the problem described here:
 * http://stackoverflow.com/questions/27341604/exception-when-using-testresttemplate 
 */
public abstract class Oauth2RESTProtectedAPIPredefinedTests extends AbstractIntegrationTests {
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	private String baseApiPath;
	
	@Value("${appverse.frontfacade.rest.remoteLogEndpoint.path:/remotelog/log}")
	private String remoteLogEndpointPath;
	
	@Value("${appverse.frontfacade.oauth2.logoutEndpoint.path:/sec/logout}")
	protected String oauth2LogoutEndpointPath;
	
	// TODO: Rename this to frontfacade oauth2
	@Value("${appverse.oauth2.implicitflow.loginEndpoint.path:/oauth/login}")
	protected String oauth2ImplicitFlowLoginEndpointPath;
	
	@Autowired
    private FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private EmbeddedWebApplicationContext server;	
		
	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private ClientDetailsService clientDetailsService;
	
	RestTemplate restTemplate = new TestRestTemplate();
	
	private String accessToken=null;
	
	// We don't use the cookie for authentication, in our workflow the client will pass basic auth
	// private String cookie;
	
	private HttpHeaders latestHeaders = null;
	
	
	@Test
	public void contextLoads() {
		assertTrue("Wrong token store type: " + tokenStore, tokenStore instanceof JdbcTokenStore);
		// assertTrue("Wrong client details type: " + clientDetailsService, JdbcClientDetailsService.class.isAssignableFrom(AopUtils.getTargetClass(clientDetailsService)));
	}


/*
	@Test
	@OAuth2ContextConfiguration(resource = AutoApproveImplicit.class, initialize = false)
	public void testPostForAutomaticApprovalToken() throws Exception {
		final ImplicitAccessTokenProvider implicitProvider = new ImplicitAccessTokenProvider();
		implicitProvider.setInterceptors(Arrays
				.<ClientHttpRequestInterceptor> asList(new ClientHttpRequestInterceptor() {
					public ClientHttpResponse intercept(HttpRequest request, byte[] body,
							ClientHttpRequestExecution execution) throws IOException {
						ClientHttpResponse result = execution.execute(request, body);
						latestHeaders = result.getHeaders();
						return result;
					}
				}));
		context.setAccessTokenProvider(implicitProvider);

		// We don't use  cookie for authentication
		// context.getAccessTokenRequest().setCookie(cookie);
		
		// We use basic auth to authenticate here as in our workflow
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", getBasicAuthentication());
		context.getAccessTokenRequest().setHeaders(headers);
		
		assertNotNull(context.getAccessToken());
		assertTrue("Wrong location header: " + latestHeaders.getLocation().getFragment(), latestHeaders.getLocation().getFragment()
				.contains("trust"));
	}
*/	
	
	static class AutoApproveImplicit extends ImplicitResourceDetails {
		public AutoApproveImplicit(Object target) {
			super();
			// TODO: All this need to be parameters passed to the test (depends on our application setup)
			setClientId("test-client-autoapprove");
			setId(getClientId());
			setPreEstablishedRedirectUri("http://anywhere");
			Oauth2RESTProtectedAPIPredefinedTests test = (Oauth2RESTProtectedAPIPredefinedTests) target;			
			setAccessTokenUri(test.http.buildUri("/oauth/authorize").toString());
			setUserAuthorizationUri(test.http.buildUri("/oauth/authorize").toString());
		}
	}	
		
	/*
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
		context.getAccessTokenRequest().add("scope.trust", "true");
		assertNotNull(context.getAccessToken());
	}
	*/

	
	static class NonAutoApproveImplicit extends ImplicitResourceDetails {
		public NonAutoApproveImplicit(Object target) {
			super();
			// TODO: All this need to be parameters passed to the test (depends on our application setup)
			setClientId("test-client");
			setId(getClientId());
			setPreEstablishedRedirectUri("http://anywhere");
		}
	}
	
	@Test
	public void testProtectedResourceIsProtected() throws Exception {
		ResponseEntity<String> response = http.getForString("/protected");
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertTrue("Wrong header: " + response.getHeaders(), response.getHeaders()
				.getFirst("WWW-Authenticate").startsWith("Bearer realm="));
	}
		
	@Test
	public void testProtectedRemoteLogWithTokenAutoApprove() throws Exception {
		// Obtains the token
		obtainTokenFromOuth2LoginEndpoint();

		// Call remote log using the token
		ResponseEntity<String> result = callRemoteLogWithAccessToken();
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	private ResponseEntity<String> callRemoteLogWithAccessToken(){
        RemoteLogRequestVO remoteLogRequest = new RemoteLogRequestVO();
        remoteLogRequest.setLogLevel("DEBUG");
        remoteLogRequest.setMessage("This is my log message!");
        
        int port = server.getEmbeddedServletContainer().getPort();
        
        HttpEntity<RemoteLogRequestVO> entity = new HttpEntity<RemoteLogRequestVO>(remoteLogRequest);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + remoteLogEndpointPath);
        builder.queryParam("access_token", accessToken);        
        
        ResponseEntity<String> result = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
        return result;        
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
	
	@Test
	@OAuth2ContextConfiguration(resource = NonAutoApproveImplicit.class, initialize = false)
	public void oauth2FlowTest() throws Exception {
		// Obtains the token
		obtainTokenFromOuth2LoginEndpoint();
		
		// Call remotelog        
		ResponseEntity<String> result = callRemoteLogWithAccessToken();
		assertEquals(HttpStatus.OK, result.getStatusCode());

		int port = server.getEmbeddedServletContainer().getPort();
		
        // We call logout endpoint (we need to use the access token for this)
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + oauth2LogoutEndpointPath);
        builder.queryParam("access_token", accessToken);        
        
        ResponseEntity<String> result2 = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, null, String.class);
        assertEquals(HttpStatus.OK, result2.getStatusCode());

        // We try to call the protected API again (after having logged out which removes the token) - We expect not to be able to call the service.
        // This will throw a exception. In this case here in the test we receive an exception but really what happened was 'access denied'
        // A production client will receive the proper http error
        result = callRemoteLogWithAccessToken();
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
	}
	
	@Test	
	public void obtainTokenFromOuth2LoginEndpoint() throws Exception {
        int port = server.getEmbeddedServletContainer().getPort();
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + oauth2ImplicitFlowLoginEndpointPath);
        builder.queryParam("username", getUsername());
        builder.queryParam("password", getPassword());
        builder.queryParam("response_type", "token");
        builder.queryParam("redirect_uri", "http://anywhere");
        
        // TODO: All this need to be parameters passed to the test (depends on our application setup)
        builder.queryParam("client_id", "test-client-autoapprove");
        
        HttpEntity<String> entity = new HttpEntity<>("");
        ResponseEntity<String> result2 = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
        
        // This means the user was correctly authenticated, then a redirection was performed to /oauth/authorize to obtain the token.
        // Then the token was sucessfully obtained (authenticating the client properly) and a last redirection was performed to the 
        // redirect_uri with the token after #
        assertEquals(HttpStatus.FOUND, result2.getStatusCode());

        // Obtain the token from redirection URL after #
        URI location = result2.getHeaders().getLocation();
        accessToken = extractToken(location.getFragment().toString());
        assertNotNull(accessToken);
	}
	
	protected String extractToken(String urlFragment){
        String[] fragmentParams = urlFragment.split("&");
        String accessToken=null;
        for (String param : fragmentParams){
        	if (param.contains("access_token")){
        		String[] params = param.split("=");
        		accessToken = params[1];
        		break;
        	}
        }
        return accessToken;
	}	

	protected abstract String getPassword();

	protected abstract String getUsername();
	
}
