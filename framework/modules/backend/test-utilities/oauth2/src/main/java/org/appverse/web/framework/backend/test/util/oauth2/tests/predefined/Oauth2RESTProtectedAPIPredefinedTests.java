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

import java.net.URI;

import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.test.util.oauth2.tests.common.AbstractIntegrationTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * Necessary to have httpclient for this tests (with scope tests) so that the http returns are handle correctly.
 * Otherwise you will experience exactly the problem described here:
 * http://stackoverflow.com/questions/27341604/exception-when-using-testresttemplate 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
public abstract class Oauth2RESTProtectedAPIPredefinedTests /*extends AbstractIntegrationTests*/ {
	
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
	private EmbeddedWebApplicationContext server;	
		
	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private ClientDetailsService clientDetailsService;
	
	RestTemplate restTemplate = new TestRestTemplate();
	
	private String accessToken=null;
	
	@Test
	public void contextLoads() {
		assertTrue("Wrong token store type: " + tokenStore, tokenStore instanceof JdbcTokenStore);
		// assertTrue("Wrong client details type: " + clientDetailsService, JdbcClientDetailsService.class.isAssignableFrom(AopUtils.getTargetClass(clientDetailsService)));
	}

	@Test
	public void testProtectedResourceIsProtected() throws Exception {
		int port = server.getEmbeddedServletContainer().getPort();
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + baseApiPath + "/protected", String.class);
		// ResponseEntity<String> response = http.getForString("/protected");
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
	
	@Test
	public void testRemoteLogIsProtected() throws Exception {
        RemoteLogRequestVO remoteLogRequest = new RemoteLogRequestVO();
        remoteLogRequest.setLogLevel("DEBUG");
        remoteLogRequest.setMessage("This is my log message!");
        
        int port = server.getEmbeddedServletContainer().getPort();

        // We call remote log WITHOUT the access token
        HttpEntity<RemoteLogRequestVO> entity = new HttpEntity<RemoteLogRequestVO>(remoteLogRequest);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + remoteLogEndpointPath);
        ResponseEntity<String> result = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
        
		assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
		assertTrue("Wrong header: " + result.getHeaders(), result.getHeaders()
				.getFirst("WWW-Authenticate").startsWith("Bearer realm="));
	}
	
	@Test
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
	
	protected ResponseEntity<String> callRemoteLogWithAccessToken(){
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
