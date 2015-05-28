package org.appverse.web.framework.backend.security;

import static org.junit.Assert.*;

import java.util.List;

import org.appverse.web.framework.backend.security.authentication.userpassword.managers.UserAndPasswordAuthenticationManager;
import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {UserAndPasswordAuthenticationTestsConfigurationApplication.class})
@WebIntegrationTest(value="server.port=0")
public class UserAndPasswordAuthenticationTests {
	
	@Autowired
	private AnnotationConfigEmbeddedWebApplicationContext context;
	
    @Autowired
	private UserAndPasswordAuthenticationManager userAndPasswordAuthenticationManager;
	
	@Test
	public void userAndPasswordAuthenticationManagerTest() throws Exception{
		// test authenticatePrincipal(String, String)
		AuthorizationData authorizationData = userAndPasswordAuthenticationManager.authenticatePrincipal("user", "password");
		assertNotNull(authorizationData);
		assertNotNull(authorizationData.getUsername());
		assertEquals(authorizationData.getUsername(), "user");
		List<String> roles = authorizationData.getRoles();
		assertNotNull(roles);
		assertEquals(roles.size(), 1);
		assertEquals(roles.get(0), "ROLE_USER");
		
		// test isPrincipalAuthenticated()
		assertEquals(userAndPasswordAuthenticationManager.isPrincipalAuthenticated(), true);
		
		// test getAuthorities();
		roles = userAndPasswordAuthenticationManager.getAuthorities();
		assertNotNull(roles);
		assertEquals(roles.size(), 1);
		assertEquals(roles.get(0), "ROLE_USER");
		
		// test getPrincipal();
		String principal = userAndPasswordAuthenticationManager.getPrincipal();
		assertNotNull(principal);
		assertEquals(principal, "user");

		// TODO: APPS-44: test pre-authentication
		/*
		List<String> roles = Arrays.asList("ROLE_USER1", "ROLE_USER2", "ROLE_USER3");
		userAndPasswordAuthenticationManager.authenticatePrincipal("user", roles);
		*/
	}
	
			
	@Configuration
	@Order(-1)
	protected static class AuthenticationManagerCustomizer extends
			GlobalAuthenticationConfigurerAdapter {

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");

			// TODO: APPS-44: Setup to test pre-authentication
			
			// http://stackoverflow.com/questions/22844236/spring-security-java-config-for-siteminder
			// http://www.coderanch.com/t/571602/Spring/Preauthentication
/*			
	        PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedGrantedAuthoritiesUserDetailsService = new PreAuthenticatedGrantedAuthoritiesUserDetailsService();
	        
	        UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> wrapper = new UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>();
	        wrapper.setUserDetailsService((UserDetailsService) preAuthenticatedGrantedAuthoritiesUserDetailsService);
	        
	        
	        PreAuthenticatedAuthenticationProvider preAuthenticatedProvider = new PreAuthenticatedAuthenticationProvider();
	        preAuthenticatedProvider.setPreAuthenticatedUserDetailsService(wrapper);
	        // preAuthenticatedProvider.setPreAuthenticatedUserDetailsService(preAuthenticatedGrantedAuthoritiesUserDetailsService);	        
	        auth.authenticationProvider(preAuthenticatedProvider);
*/	        
		}
	}

}
