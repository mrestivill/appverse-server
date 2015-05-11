package org.appverse.web.framework.backend.security.oauth2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;

/*
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;
import org.appverse.web.framework.backend.security.xs.SecurityHelper;
*/
// import org.appverse.web.framework.backend.security.xs.xsrf.XSRFCheckFilterTests.AuthenticationManagerCustomizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitResourceDetails;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
public class Oauth2ProtectedRemoteLogTests extends AbstractIntegrationTests {
		
	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private ClientDetailsService clientDetailsService;
	
	RestTemplate restTemplate = new TestRestTemplate();
	
	/*
	 * Enable this init method if you need to use a proxy to debug (fiddler, for instance)
	 * This is required as passing regular JVM arguments for proxy setup seems not to work with RestTemplate
	 * as it uses Apache HttpClient 
    @Before
    public void initProxy(){    	
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
	    Proxy proxy= new Proxy(Type.HTTP, new InetSocketAddress("localhost", 8888));
	    requestFactory.setProxy(proxy);
	    restTemplate = new RestTemplate(requestFactory);    	
    }
	 */
    
	protected String getPassword() {
		return "secret";
	}

	protected String getUsername() {
		return "dave";
	}

	@Test
	public void contextLoads() {
		assertTrue("Wrong token store type: " + tokenStore, tokenStore instanceof JdbcTokenStore);
		assertTrue("Wrong client details type: " + clientDetailsService, JdbcClientDetailsService.class.isAssignableFrom(AopUtils.getTargetClass(clientDetailsService)));
	}
	
	/*
	@Test
	public void remoteLogServiceEnabledTest() {
		int port = context.getEmbeddedServletContainer().getPort();
		RemoteLogRequestVO logRequestVO = new RemoteLogRequestVO();
		logRequestVO.setMessage("Test mesage!");
		logRequestVO.setLogLevel("DEBUG");
		 
		ResponseEntity<String> entity = restTemplate.postForEntity("http://localhost:" + port + "/remotelog/log", logRequestVO, String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}
	*/
	
	/* Crash with MVC
	 * 
	 * http://forum.spring.io/forum/spring-projects/security/oauth/109730-oauth-2-without-spring-mvc
	 * http://stackoverflow.com/questions/21907777/spring-security-oauth-basic-access-authentication-needed-when-sending-token-re
	 */
	
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
			setClientId("my-trusted-client");
			setId(getClientId());
			setPreEstablishedRedirectUri("http://anywhere");
		}
	}
	
	
	/*
	@Test
	public void testFilterWithCorrectXSRFToken() throws Exception{		
		int port = context.getEmbeddedServletContainer().getPort();
		
		// Step 1: Login and obtaining a XSRF Token
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + new String(Base64.encode("user:password".getBytes("UTF-8"))));
		HttpEntity<String> entity = new HttpEntity<String>("headers", headers);

		ResponseEntity<AuthorizationData> responseEntity = restTemplate.exchange("http://localhost:" + port + "/sec/login", HttpMethod.POST, entity, AuthorizationData.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		
		List<String> xsrfTokenHeaders = responseEntity.getHeaders().get(SecurityHelper.XSRF_TOKEN_NAME);
		assertNotNull(xsrfTokenHeaders);
		assertEquals(xsrfTokenHeaders.size(), 1);
		assertNotNull(xsrfTokenHeaders.get(0));
				
		AuthorizationData authorizationData = responseEntity.getBody();
		assertNotNull(authorizationData);
		List<String> roles = authorizationData.getRoles();
		assertNotNull(roles);
		assertEquals(roles.size(), 1);
		assertEquals(roles.get(0), "ROLE_USER");
		
		// Step 2: Using the XSRF Token calling a protected resource
		headers = new HttpHeaders();
		String JSESSIONID_HEADER = responseEntity.getHeaders().getFirst("Set-Cookie");		
		headers.set(HttpHeaders.COOKIE, JSESSIONID_HEADER);
		
		// Content type need to be specified otherwise we receive a 415
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		RemoteLogRequestVO logRequestVO = new RemoteLogRequestVO();
		logRequestVO.setMessage("Test mesage!");
		logRequestVO.setLogLevel("DEBUG");
		
		ResponseEntity<String> logResponseEntity = restTemplate.exchange("http://localhost:" + port + "/remotelog/log", HttpMethod.POST, new HttpEntity<RemoteLogRequestVO>(logRequestVO, headers), String.class);
		assertEquals(HttpStatus.OK, logResponseEntity.getStatusCode());
	}
	*/
	
	
}
