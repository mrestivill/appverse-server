package org.appverse.web.framework.backend.frontfacade.rest;

import org.appverse.web.framework.backend.frontfacade.rest.beans.ResponseDataVO;
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

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {FrontFacadeModuleTestsConfigurationApplication.class})
@WebIntegrationTest(randomPort= true)
public class MvcExceptionHandlerTests {
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	private String baseApiPath;
	
	@Autowired
	private AnnotationConfigEmbeddedWebApplicationContext context;
	
	RestTemplate restTemplate = new TestRestTemplate();

    
    @PostConstruct
    public void init() {
    }
 
    /**
     * Test if response is the one expected from the jersey resource..
     */
    @Test
    public void test() throws Exception {
    	int port = context.getEmbeddedServletContainer().getPort();

    	HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + new String(Base64.encode("user:password".getBytes("UTF-8"))));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		 
		ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:" + port + baseApiPath + "/hello", HttpMethod.GET, entity, String.class);
    	
        String data = responseEntity.getBody();
        assertEquals("Hello World!", data);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testNotFound() throws Exception {
        int port = context.getEmbeddedServletContainer().getPort();

        HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + new String(Base64.encode("user:password".getBytes("UTF-8"))));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		 
		ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:" + port + baseApiPath + "/badurl", HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
    
    /**
     * Test if the response comes from JerseyExceptionHandler.
     */
    @Test
    public void testExceptionHandler() throws Exception {
    	int port = context.getEmbeddedServletContainer().getPort();

    	HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + new String(Base64.encode("user:password".getBytes("UTF-8"))));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		 
		ResponseEntity<ResponseDataVO> responseEntity = restTemplate.exchange("http://localhost:" + port + baseApiPath + "/exc", HttpMethod.GET, entity, ResponseDataVO.class);
    	
        ResponseDataVO data = responseEntity.getBody();
        assertEquals(500, data.getErrorVO().getCode());
        assertTrue("contains message", data.getErrorVO().getMessage().contains("kk"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

    }
}
