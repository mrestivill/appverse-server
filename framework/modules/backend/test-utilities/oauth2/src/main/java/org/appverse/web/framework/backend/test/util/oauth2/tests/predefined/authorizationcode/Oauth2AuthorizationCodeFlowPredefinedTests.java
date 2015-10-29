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
package org.appverse.web.framework.backend.test.util.oauth2.tests.predefined.authorizationcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
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
public abstract class Oauth2AuthorizationCodeFlowPredefinedTests {
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	protected String baseApiPath;
	
	@Value("${appverse.frontfacade.rest.remoteLogEndpoint.path:/remotelog/log}")
	protected String remoteLogEndpointPath;
	
	@Value("${appverse.frontfacade.oauth2.logoutEndpoint.path:/sec/logout}")
	protected String oauth2LogoutEndpointPath;
	
	@Value("${appverse.frontfacade.oauth2.loginEndpoint.path:/sec/login}")
	protected String oauth2ImplicitFlowLoginEndpointPath;
	
	@Value("${appverse.frontfacade.oauth2.tokenEndpoint.path:/sec/token}")
	protected String oauth2TokenEndpointPath;
	
	protected int port;
		
	@Autowired
	private EmbeddedWebApplicationContext server;	
		
	@Autowired
	private TokenStore tokenStore;
	
	RestTemplate restTemplate = new TestRestTemplate();
	
	private String accessToken=null;
	private String authorizationCode=null;
	private String refreshToken=null;
	
	@Before
	public void init(){
		 port = server.getEmbeddedServletContainer().getPort();
	}	
	
	@Test
	public void contextLoads() {
		assertTrue("Wrong token store type: " + tokenStore, tokenStore instanceof TokenStore);
	}

	@Test
	public void testProtectedResourceIsProtected() throws Exception {
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + baseApiPath + "/protected", String.class);
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
		
		// Call remotelog once the access token has expired (we wait enough to make sure it has expired)
        Thread.sleep(getTokenExpirationDelayInSeconds()*1000);
        
		// Call remotelog        
		result = callRemoteLogWithAccessToken();
		assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
		assertTrue(result.getBody().contains("Access token expired"));
		
		// Refresh the token
		refreshToken();        

        // We call logout endpoint (we need to use the access token for this)
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + oauth2LogoutEndpointPath);
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
	public void obtainAuthorizationCode() throws Exception {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + oauth2ImplicitFlowLoginEndpointPath);
        builder.queryParam("client_id", getClientId());        
        builder.queryParam("response_type", "code");
        builder.queryParam("redirect_uri", "http://anywhere");
        // builder.queryParam("realm","oauth2-resource");
        
        // optional builder.queryParam("scope", "");
        // recommended (optional) builder.queryParam("state", "");
        
        // Add Basic Authorization headers for USER authentication
        HttpHeaders headers = new HttpHeaders();
        Encoder encoder = Base64.getEncoder();
        headers.add("Authorization","Basic " + encoder.encodeToString((getUsername() + ":" + getPassword()).getBytes()));

        HttpEntity<String> entity = new HttpEntity("",headers);
        ResponseEntity<String> result2 = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);
        
        // check this! assertEquals(HttpStatus.FOUND, result2.getStatusCode());        

        // Obtain the token from redirection URL after #
        URI location = result2.getHeaders().getLocation();
        authorizationCode = extractAuthorizationCode(location.toString());
        assertNotNull(authorizationCode);
	}
	
	
	@Test	
	public void obtainTokenFromOuth2LoginEndpoint() throws Exception {
		obtainAuthorizationCode();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/oauth/token");
        // Here we don't authenticate the user, we authenticate the client and we pass the authcode proving that the user has accepted and loged in        
        builder.queryParam("client_id", getClientId());
        builder.queryParam("grant_type", "authorization_code");
        builder.queryParam("code", authorizationCode);
        builder.queryParam("redirect_uri", "http://anywhere");

        // Add Basic Authorization headers for CLIENT authentication (user was authenticated in previous request (authorization code)
        HttpHeaders headers = new HttpHeaders();
        Encoder encoder = Base64.getEncoder();
        headers.add("Authorization","Basic " + encoder.encodeToString((getClientId() + ":" + getClientSecret()).getBytes()));
        
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<OAuth2AccessToken> result2 = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, OAuth2AccessToken.class);
        
        // This means the user was correctly authenticated, then a redirection was performed to /oauth/authorize to obtain the token.
        // Then the token was sucessfully obtained (authenticating the client properly) and a last redirection was performed to the 
        // redirect_uri with the token after #
        assertEquals(HttpStatus.OK, result2.getStatusCode());
        
        // Obtain and keep the token
        accessToken = result2.getBody().getValue();
        assertNotNull(accessToken);        
        
        refreshToken = result2.getBody().getRefreshToken().getValue();
        assertNotNull(refreshToken);
	}
	
	public void refreshToken(){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/oauth/token");
        // Here we don't authenticate the user, we authenticate the client and we pass the authcode proving that the user has accepted and loged in
        builder.queryParam("grant_type", "refresh_token");
        builder.queryParam("refresh_token", refreshToken);

        // Add Basic Authorization headers for CLIENT authentication (user was authenticated in previous request (authorization code)
        HttpHeaders headers = new HttpHeaders();
        Encoder encoder = Base64.getEncoder();
        headers.add("Authorization","Basic " + encoder.encodeToString((getClientId() + ":" + getClientSecret()).getBytes()));
        
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        ResponseEntity<OAuth2AccessToken> result2 = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, OAuth2AccessToken.class);
        
        assertEquals(HttpStatus.OK, result2.getStatusCode());
        
        // Obtain and keep the token
        accessToken = result2.getBody().getValue();
        assertNotNull(accessToken);        
        
        refreshToken = result2.getBody().getRefreshToken().getValue();
        assertNotNull(refreshToken);
	}
	
	protected ResponseEntity<String> callRemoteLogWithAccessToken(){
        RemoteLogRequestVO remoteLogRequest = new RemoteLogRequestVO();
        remoteLogRequest.setLogLevel("DEBUG");
        remoteLogRequest.setMessage("This is my log message!");
        
        HttpEntity<RemoteLogRequestVO> entity = new HttpEntity<RemoteLogRequestVO>(remoteLogRequest);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + remoteLogEndpointPath);
        builder.queryParam("access_token", accessToken);        
        
        ResponseEntity<String> result = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
        return result;        
	}	

	protected String extractAuthorizationCode(String urlFragment){
		return extractParam(urlFragment, "code");
	}

	protected String extractParam(String urlFragment, String paramName){
        String[] fragmentParams = urlFragment.split("&");
        String extractedParam=null;
        for (String param : fragmentParams){
        	if (param.contains(paramName)){
        		String[] params = param.split("=");
        		extractedParam = params[1];
        		break;
        	}
        }
        return extractedParam;
	}	
	

	protected abstract String getPassword();

	protected abstract String getUsername();
	
	protected abstract String getClientId();
	
	protected abstract String getClientSecret();
	
	protected abstract int getTokenExpirationDelayInSeconds();
	
}
