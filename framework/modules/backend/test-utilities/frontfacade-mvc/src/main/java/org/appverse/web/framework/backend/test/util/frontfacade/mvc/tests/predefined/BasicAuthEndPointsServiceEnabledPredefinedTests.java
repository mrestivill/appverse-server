/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0�?). If a copy of the APL was not distributed with this 
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
package org.appverse.web.framework.backend.test.util.frontfacade.mvc.tests.predefined;

import static org.junit.Assert.assertEquals;

import org.appverse.web.framework.backend.frontfacade.rest.beans.CredentialsVO;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;
import org.appverse.web.framework.backend.test.util.frontfacade.BaseAbstractAuthenticationRequiredTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort= true)
public abstract class BasicAuthEndPointsServiceEnabledPredefinedTests extends BaseAbstractAuthenticationRequiredTest {
	
	@Value("${appverse.frontfacade.rest.remoteLogEndpoint.path:/remotelog/log}")
	protected String remoteLogEndpointPath;	
	
	/*
	 * Basic Authentication endpoint tests
	 */
	
	@Test
	public void basicAuthenticationServiceTest() throws Exception{
		login();
	}
	
	@Test
	public void basicAuthenticationServiceTestInvalidCredentials() throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + new String(Base64.encode("user:badpassword".getBytes("UTF-8"))));
		HttpEntity<String> entity = new HttpEntity<String>("headers", headers);

		ResponseEntity<AuthorizationData> responseEntity = restTemplate.exchange("http://localhost:" + port + baseApiPath + basicAuthenticationEndpointPath, HttpMethod.POST, entity, AuthorizationData.class);
		assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
	}
	
	
	@Test
	public void basicAuthenticationRemoteLogServiceEnabledTest() throws Exception {
		TestLoginInfo loginInfo = login();
		
		RemoteLogRequestVO logRequestVO = new RemoteLogRequestVO();
		logRequestVO.setMessage("Test mesage!");
		logRequestVO.setLogLevel("DEBUG");
	
		// This test requires the test CSRF Token. This implies passing JSESSIONID and CSRF Token
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", loginInfo.getJsessionid());
		HttpEntity<RemoteLogRequestVO> entity = new HttpEntity<RemoteLogRequestVO>(logRequestVO, headers);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + remoteLogEndpointPath)
				.queryParam(DEFAULT_CSRF_PARAMETER_NAME, loginInfo.getXsrfToken());
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
	
	@Test
	public void basicAuthenticationRemoteLogServiceEnabledWithoutCsrfTokenTest() throws Exception {
		RemoteLogRequestVO logRequestVO = new RemoteLogRequestVO();
		logRequestVO.setMessage("Test mesage!");
		logRequestVO.setLogLevel("DEBUG");
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + new String(Base64.encode((getUsername() + ":" + getPassword()).getBytes("UTF-8"))));
		HttpEntity<RemoteLogRequestVO> entity = new HttpEntity<RemoteLogRequestVO>(logRequestVO, headers);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + remoteLogEndpointPath);
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
		assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
	}
				
	
	@Test
	public void basicAuthenticationFlowTest() throws Exception{
		// Login first
		TestLoginInfo loginInfo = login();

		// Calling protected remotelog service
		RemoteLogRequestVO logRequestVO = new RemoteLogRequestVO();
		logRequestVO.setMessage("Test mesage!");
		logRequestVO.setLogLevel("DEBUG");		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", loginInfo.getJsessionid());
		HttpEntity<RemoteLogRequestVO> entityRemotelog = new HttpEntity<RemoteLogRequestVO>(logRequestVO, headers);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + remoteLogEndpointPath);
		// Try without token first - It should be 'Forbidden'
		// http://springinpractice.com/2012/04/08/sending-cookies-with-resttemplate		
		ResponseEntity<String> responseEntityRemotelog = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entityRemotelog, String.class);
		assertEquals(HttpStatus.FORBIDDEN, responseEntityRemotelog.getStatusCode());

		// Try now with the CSRF token - It should work well
		// This implies passing JSESSIONID and CSRF Token
		headers.set(DEFAULT_CSRF_HEADER_NAME, loginInfo.getXsrfToken());
		entityRemotelog = new HttpEntity<RemoteLogRequestVO>(logRequestVO, headers);
		responseEntityRemotelog = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entityRemotelog, String.class);
		assertEquals(HttpStatus.OK, responseEntityRemotelog.getStatusCode());
		
		// TODO
		// Calling here logout
		
	}
	
	/*
	 * Simple Authentication (for testing and swagger purposes) endpoint tests
	 */
	
	@Test
	public void simpleAuthenticationServiceTest() throws Exception{
		simpleLogin();
	}		

	@Test
	public void simpleAuthenticationServiceTestNoCredentials() throws Exception{
		CredentialsVO credentialsVO = new CredentialsVO();
		HttpEntity<CredentialsVO> entity = new HttpEntity<CredentialsVO>(credentialsVO);

		ResponseEntity<AuthorizationData> responseEntity = restTemplate.exchange("http://localhost:" + port + baseApiPath + simpleAuthenticationEndpointPath, HttpMethod.POST, entity, AuthorizationData.class);
		assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
	}
	@Test
	public void simpleAuthenticationServiceTestInvalidCredentials() throws Exception{
		CredentialsVO credentialsVO = new CredentialsVO();
		credentialsVO.setUsername("user");
		credentialsVO.setPassword("badpassword");
		HttpEntity<CredentialsVO> entity = new HttpEntity<CredentialsVO>(credentialsVO);

		ResponseEntity<AuthorizationData> responseEntity = restTemplate.exchange("http://localhost:" + port + baseApiPath + simpleAuthenticationEndpointPath, HttpMethod.POST, entity, AuthorizationData.class);
		assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
	}
	
	@Test
	public void simpleAuthenticationRemoteLogServiceEnabledTest() throws Exception {
		TestLoginInfo loginInfo = simpleLogin();
		
		RemoteLogRequestVO logRequestVO = new RemoteLogRequestVO();
		logRequestVO.setMessage("Test mesage!");
		logRequestVO.setLogLevel("DEBUG");
	
		// This test requires the test CSRF Token. This implies passing JSESSIONID and CSRF Token
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", loginInfo.getJsessionid());
		HttpEntity<RemoteLogRequestVO> entity = new HttpEntity<RemoteLogRequestVO>(logRequestVO, headers);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + remoteLogEndpointPath)
				.queryParam(DEFAULT_CSRF_PARAMETER_NAME, loginInfo.getXsrfToken());
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
	
	@Test
	public void simpleAuthenticationRemoteLogServiceEnabledWithoutCsrfTokenTest() throws Exception {
		RemoteLogRequestVO logRequestVO = new RemoteLogRequestVO();
		logRequestVO.setMessage("Test mesage!");
		logRequestVO.setLogLevel("DEBUG");
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + new String(Base64.encode((getUsername() + ":" + getPassword()).getBytes("UTF-8"))));
		HttpEntity<RemoteLogRequestVO> entity = new HttpEntity<RemoteLogRequestVO>(logRequestVO, headers);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + remoteLogEndpointPath);
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
		assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
	}
				
	
	@Test
	public void simpleAuthenticationFlowTest() throws Exception{
		// Login first
		TestLoginInfo loginInfo = login();

		// Calling protected remotelog service
		RemoteLogRequestVO logRequestVO = new RemoteLogRequestVO();
		logRequestVO.setMessage("Test mesage!");
		logRequestVO.setLogLevel("DEBUG");		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", loginInfo.getJsessionid());
		HttpEntity<RemoteLogRequestVO> entityRemotelog = new HttpEntity<RemoteLogRequestVO>(logRequestVO, headers);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + remoteLogEndpointPath);
		// Try without token first - It should be 'Forbidden'
		// http://springinpractice.com/2012/04/08/sending-cookies-with-resttemplate		
		ResponseEntity<String> responseEntityRemotelog = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entityRemotelog, String.class);
		assertEquals(HttpStatus.FORBIDDEN, responseEntityRemotelog.getStatusCode());

		// Try now with the CSRF token - It should work well
		// This implies passing JSESSIONID and CSRF Token
		headers.set(DEFAULT_CSRF_HEADER_NAME, loginInfo.getXsrfToken());
		entityRemotelog = new HttpEntity<RemoteLogRequestVO>(logRequestVO, headers);
		responseEntityRemotelog = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entityRemotelog, String.class);
		assertEquals(HttpStatus.OK, responseEntityRemotelog.getStatusCode());
		
		// TODO
		// Calling here logout
		
	}
			
}
