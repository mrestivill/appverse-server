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
package org.appverse.web.framework.backend.test.util.frontfacade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.PostConstruct;

import org.appverse.web.framework.backend.frontfacade.rest.beans.CredentialsVO;
import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;
import org.appverse.web.framework.backend.test.util.frontfacade.mvc.tests.predefined.TestLoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

public abstract class BaseAbstractAuthenticationRequiredTest {
	
	// The following constants need to match the CsrfRepository you are using
	// In particular this test is designed to work with a TestCsrfTokenRepository
	protected static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";
	
	protected static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	protected String baseApiPath;
	
	@Value("${appverse.frontfacade.rest.basicAuthenticationEndpoint.path:/sec/login}")
	protected String basicAuthenticationEndpointPath;

	@Value("${appverse.frontfacade.rest.simpleAuthenticationEndpoint.path:/sec/simplelogin}")
	protected String simpleAuthenticationEndpointPath;
		
	@Autowired
	protected AnnotationConfigEmbeddedWebApplicationContext context;	
	
	protected RestTemplate restTemplate = new TestRestTemplate();	
	
	protected int port;
	
	@PostConstruct
	private void init(){
		 port = context.getEmbeddedServletContainer().getPort();
	}	
	
	protected TestLoginInfo login() throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + new String(Base64.encode((getUsername() + ":" + getPassword()).getBytes("UTF-8"))));
		HttpEntity<String> entity = new HttpEntity<String>("headers", headers);

		ResponseEntity<AuthorizationData> responseEntity = restTemplate.exchange("http://localhost:" + port + baseApiPath + basicAuthenticationEndpointPath, HttpMethod.POST, entity, AuthorizationData.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
				
		List<String> xsrfTokenHeaders = responseEntity.getHeaders().get(DEFAULT_CSRF_HEADER_NAME);
		assertNotNull(xsrfTokenHeaders);
		assertEquals(xsrfTokenHeaders.size(), 1);
		assertNotNull(xsrfTokenHeaders.get(0));
		AuthorizationData authorizationData = responseEntity.getBody();
		assertNotNull(authorizationData);
		List<String> roles = authorizationData.getRoles();
		assertNotNull(roles);
		assertEquals(roles.size() > 0, true);
		assertEquals(roles.contains(getAnUserRole()), true);		
		
		TestLoginInfo loginInfo = new TestLoginInfo();
		loginInfo.setXsrfToken(xsrfTokenHeaders.get(0));
		loginInfo.setAuthorizationData(authorizationData);
		loginInfo.setJsessionid(responseEntity.getHeaders().getFirst("Set-Cookie"));
		return loginInfo;
	}
	
	protected TestLoginInfo simpleLogin() throws Exception{
		CredentialsVO credentialsVO = new CredentialsVO();
		credentialsVO.setUsername(getUsername());
		credentialsVO.setPassword(getPassword());
		HttpEntity<CredentialsVO> entity = new HttpEntity<CredentialsVO>(credentialsVO);

		ResponseEntity<AuthorizationData> responseEntity = restTemplate.exchange("http://localhost:" + port + baseApiPath + simpleAuthenticationEndpointPath, HttpMethod.POST, entity, AuthorizationData.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		List<String> xsrfTokenHeaders = responseEntity.getHeaders().get(DEFAULT_CSRF_HEADER_NAME);
		assertNotNull(xsrfTokenHeaders);
		assertEquals(xsrfTokenHeaders.size(), 1);
		assertNotNull(xsrfTokenHeaders.get(0));
		AuthorizationData authorizationData = responseEntity.getBody();
		assertNotNull(authorizationData);
		List<String> roles = authorizationData.getRoles();
		assertNotNull(roles);
		assertEquals(roles.size() > 0, true);
		assertEquals(roles.contains(getAnUserRole()), true);
		
		TestLoginInfo loginInfo = new TestLoginInfo();
		loginInfo.setXsrfToken(xsrfTokenHeaders.get(0));
		loginInfo.setAuthorizationData(authorizationData);
		loginInfo.setJsessionid(responseEntity.getHeaders().getFirst("Set-Cookie"));
		return loginInfo;
	}		
		
	protected abstract String getPassword();

	protected abstract String getUsername();
	
	protected abstract String getAnUserRole();

}
