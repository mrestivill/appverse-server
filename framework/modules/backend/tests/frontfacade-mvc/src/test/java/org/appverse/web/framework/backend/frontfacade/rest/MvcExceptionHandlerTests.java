package org.appverse.web.framework.backend.frontfacade.rest;

import org.appverse.web.framework.backend.frontfacade.rest.beans.ResponseDataVO;
import org.appverse.web.framework.backend.test.util.frontfacade.mvc.tests.predefined.BaseAbstractAuthenticationRequiredTest;
import org.appverse.web.framework.backend.test.util.frontfacade.mvc.tests.predefined.TestLoginInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {FrontFacadeModuleTestsConfigurationApplication.class})
@WebIntegrationTest(randomPort= true, value="appverse.security.xs.xsrf.filter.enabled=false")
public class MvcExceptionHandlerTests extends BaseAbstractAuthenticationRequiredTest{
	 
    @Test
    public void test() throws Exception {
    	// Login first
    	TestLoginInfo loginInfo = login();
    	HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", loginInfo.getJsessionid());
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// Calling protected resource - requires CSRF token
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + "/hello")
				.queryParam(DEFAULT_CSRF_PARAMETER_NAME, loginInfo.getXsrfToken());
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);
    	
        String data = responseEntity.getBody();
        assertEquals("Hello World!", data);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testNotFound() throws Exception {
    	// Login first
    	TestLoginInfo loginInfo = login();
    	HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", loginInfo.getJsessionid());
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// Calling protected resource - requires CSRF token
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + "/badurl")
				.queryParam(DEFAULT_CSRF_PARAMETER_NAME, loginInfo.getXsrfToken());
    	
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
    
    @Test
    public void testExceptionHandler() throws Exception {
    	// Login first
    	TestLoginInfo loginInfo = login();
    	HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", loginInfo.getJsessionid());
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		// Calling protected resource - requires CSRF token
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + "/exc")
				.queryParam(DEFAULT_CSRF_PARAMETER_NAME, loginInfo.getXsrfToken());
		
		ResponseEntity<ResponseDataVO> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, ResponseDataVO.class);
    	
        ResponseDataVO data = responseEntity.getBody();
        assertEquals(500, data.getErrorVO().getCode());
        assertTrue("contains message", data.getErrorVO().getMessage().contains("kk"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

    }

	@Override
	protected String getPassword() {
		return "password";
	}

	@Override
	protected String getUsername() {
		return"user";
	}

	@Override
	protected String getAnUserRole() {
		return "ROLE_USER";
	}			
}
