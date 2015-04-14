package org.appverse.web.framework.backend.frontfacade.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;
import org.appverse.web.framework.backend.security.xs.SecurityHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BasicAuthenticationServiceTestsConfigurationApplication.class})
@WebIntegrationTest("security.basic.enabled=false")
public class BasicAuthenticationServiceTests {
	
	@Autowired
	private AnnotationConfigEmbeddedWebApplicationContext context;	

	RestTemplate restTemplate = new TestRestTemplate();
	
	@Test
	public void basicAuthenticationServiceTest() throws Exception{
		
		this.context.register(AuthenticationManagerCustomizer.class);
		
		int port = context.getEmbeddedServletContainer().getPort();
		 
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
	}
	
			
	@Configuration
	@Order(-1)
	protected static class AuthenticationManagerCustomizer extends
			GlobalAuthenticationConfigurerAdapter {

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
		}

	}

}
