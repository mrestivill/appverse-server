package org.appverse.web.framework.backend.test.util.frontfacade.mvc.tests.predefined;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.PostConstruct;

import org.appverse.web.framework.backend.frontfacade.rest.beans.CredentialsVO;
import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;
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
