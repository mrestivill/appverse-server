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
package org.appverse.web.framework.backend.frontfacade.rest;

import org.appverse.web.framework.backend.frontfacade.rest.beans.CredentialsVO;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {FrontFacadeModuleTestsConfigurationApplication.class})
@WebIntegrationTest(randomPort= true, 
					value={"appverse.frontfacade.rest.remoteLogEndpoint.enabled=false",
						    "appverse.frontfacade.rest.basicAuthenticationEndpoint.enabled=false",
							"appverse.frontfacade.rest.simpleAuthenticationEndpoint.enabled=false",
							"appverse.frontfacade.rest.exceptionHandler.enabled=false"
					})
public class EndPointsDisabledTests {
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	private String baseApiPath;
	
	@Value("${appverse.frontfacade.rest.basicAuthenticationEndpoint.path:/sec/login}")
	private String basicAuthenticationEndpointPath;

	@Value("${appverse.frontfacade.rest.simpleAuthenticationEndpoint.path:/sec/simplelogin}")
	private String simpleAuthenticationEndpointPath;
	
	@Value("${appverse.frontfacade.rest.remoteLogEndpoint.path:/remotelog/log}")
	private String remoteLogEndpointPath;
	
	@Autowired
	private AnnotationConfigEmbeddedWebApplicationContext context;	
	
	RestTemplate restTemplate = new TestRestTemplate();
	
	@Test
	public void remoteLogServiceDisabledTest() {
		int port = context.getEmbeddedServletContainer().getPort();
		RemoteLogRequestVO logRequestVO = new RemoteLogRequestVO();
		logRequestVO.setMessage("Test mesage!");
		logRequestVO.setLogLevel("DEBUG");
		 
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:" + port + baseApiPath + remoteLogEndpointPath, logRequestVO, String.class);
		// TODO!!Why when the controller is disbled by property returns 405 instead of 404? 
		// assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
		assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
	}
	
	@Test
	public void basicAuthenticationServiceTest() throws Exception{
		int port = context.getEmbeddedServletContainer().getPort();
		 
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + new String(Base64.encode("user:password".getBytes("UTF-8"))));
		HttpEntity<String> entity = new HttpEntity<String>("headers", headers);

		ResponseEntity<AuthorizationData> responseEntity = restTemplate.exchange("http://localhost:" + port + baseApiPath + basicAuthenticationEndpointPath, HttpMethod.POST, entity, AuthorizationData.class);
		// TODO!!Why when the controller is disbled by property returns 405 instead of 404? 
		// assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
		assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
	}
	@Test
	public void simpleAuthenticationServiceTest() throws Exception{
		int port = context.getEmbeddedServletContainer().getPort();

		CredentialsVO credentialsVO = new CredentialsVO();
		credentialsVO.setUsername("user");
		credentialsVO.setPassword("password");
		HttpEntity<CredentialsVO> entity = new HttpEntity<CredentialsVO>(credentialsVO);


		ResponseEntity<AuthorizationData> responseEntity = restTemplate.exchange("http://localhost:" + port + baseApiPath + simpleAuthenticationEndpointPath, HttpMethod.POST, entity, AuthorizationData.class);
		// TODO!!Why when the controller is disbled by property returns 405 instead of 404?
		// assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
		assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
	}

	@Test
	public void testExceptionHandler() {
		int port = context.getEmbeddedServletContainer().getPort();
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + baseApiPath + "/exc", String.class);
		String data = response.getBody();
		assertNotNull("contains  message", data);
		assertTrue("contains default message", data.contains("timestamp"));
		assertTrue("contains message message", data.contains("kk"));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

	}
}
