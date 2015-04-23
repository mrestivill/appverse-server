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
package org.appverse.web.framework.backend.security.xs.xsrf;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {XSRFModuleTestsConfigurationApplication.class})
@WebIntegrationTest(randomPort= true, 
					value={"security.basic.enabled=false",
		                   /*
						   "appverse.security.xs.xsrffilter.urlPattern=/rest/*",
						   "appverse.security.xs.xsrffilter.match=*",
						   "appverse.security.xs.xsrffilter.exclude=",
						   "appverse.security.xs.xsrffilter.wildcards=false",
						   "appverse.security.xs.xsrffilter.getXsrfPath=getXSRFSessionToken"
						   */})
public class XSRFCheckFilterTests {
	
	@Autowired
	private AnnotationConfigEmbeddedWebApplicationContext context;
	
	RestTemplate restTemplate = new TestRestTemplate();
	
	/*
	 <filter>
        <filter-name>XSRFFilter</filter-name>
        <filter-class>org.appverse.web.framework.backend.api.helpers.security.XSRFCheckFilter</filter-class>
             <init-param>
                <param-name>match</param-name>
                <param-value>*</param-value>
            </init-param>
            <init-param>
            	<param-name>wildcards</param-name>
            	<param-value>false</param-value>
            </init-param>
            <init-param>
                <param-name>getXsrfPath</param-name>
                <param-value>getXSRFSessionToken</param-value>
            </init-param>
    </filter>
    <filter-mapping>
    	<filter-name>XSRFFilter</filter-name>
    	<url-pattern>/admin/rest/*</url-pattern>
    </filter-mapping>
    
	private Boolean wildcards;
	
	private String match;
	
	private String exclude;
	
	private String getXsrfPath;
	
	private String urlPattern;    
	 */	
	
	
	@Test
	public void testInitParameterConfiguration() {
		FilterRegistrationBean registrationBean = this.context.getBean("gzipFilter",
				FilterRegistrationBean.class);
		assertThat(registrationBean.getInitParameters().size(), equalTo(5));
		assertThat(registrationBean.getInitParameters().get("urlPattern"),
				equalTo("/rest/*"));		
		assertThat(registrationBean.getInitParameters().get("match"),
				equalTo("*"));
		assertThat(registrationBean.getInitParameters().get("wildcards"),
				equalTo("false"));
		assertThat(registrationBean.getInitParameters().get("getXsrfPath"),
				equalTo("getXSRFSessionToken"));
		assertThat(registrationBean.getInitParameters().get("exclude"),
				equalTo(""));
	}
	
	// TODO: We can't add the following tests here as backend-frontfacade-rest has a dependency of this module.
	// To do this test we need to add a dependency to backend-frontfacade-rest resulting in a cyclic dependency.
	// In fact, this would be an integration test that we should write separately (like JPA tests) and just 
	// provide here a test for the autoconfiguration itself. The filter we would test it in the integration test...
	
	/*
	@Test
	public void testFilterWithCorrectXSRFToken() throws Exception{		
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
	*/
	
	
	/*
	@Test
	public void remoteLogServiceDisabledTest() {
		int port = context.getEmbeddedServletContainer().getPort();
		RemoteLogRequestVO logRequestVO = new RemoteLogRequestVO();
		logRequestVO.setMessage("Test mesage!");
		logRequestVO.setLogLevel("DEBUG");
		 
		ResponseEntity<String> entity = restTemplate.postForEntity("http://localhost:" + port + "/remotelog/log", logRequestVO, String.class);
		assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
	}
	
	@Test
	public void basicAuthenticationServiceTest() throws Exception{
		int port = context.getEmbeddedServletContainer().getPort();
		 
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + new String(Base64.encode("user:password".getBytes("UTF-8"))));
		HttpEntity<String> entity = new HttpEntity<String>("headers", headers);

		ResponseEntity<AuthorizationData> responseEntity = restTemplate.exchange("http://localhost:" + port + "/sec/login", HttpMethod.POST, entity, AuthorizationData.class);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}
	*/
}
