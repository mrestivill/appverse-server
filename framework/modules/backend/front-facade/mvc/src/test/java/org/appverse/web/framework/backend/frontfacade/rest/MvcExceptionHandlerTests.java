package org.appverse.web.framework.backend.frontfacade.rest;

import org.appverse.web.framework.backend.frontfacade.rest.beans.ResponseDataVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {FrontFacadeModuleTestsConfigurationApplication.class})
@WebIntegrationTest(randomPort= true, value={"security.basic.enabled=false","debug=true",
        "appverse.frontfacade.rest.remoteLogEndpoint.enabled=false",
        "appverse.frontfacade.rest.basicAuthenticationEndpoint.enabled=false",
        "appverse.frontfacade.rest.simpleAuthenticationEndpoint.enabled=false"})
public class MvcExceptionHandlerTests {
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
    public void test() {
    	int port = context.getEmbeddedServletContainer().getPort();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/api/hello", String.class);
        String data = response.getBody();
        assertEquals("Hello World!", data);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    /**
     * Test if the response comes from JerseyExceptionHandler.
     */
    @Test
    public void testExceptionHandler() {
    	int port = context.getEmbeddedServletContainer().getPort();
        ResponseEntity<ResponseDataVO> response = restTemplate.getForEntity("http://localhost:"+port+"/api/exc", ResponseDataVO.class);
        ResponseDataVO data = response.getBody();
        assertEquals(500, data.getErrorVO().getCode());
        assertTrue("contains message", data.getErrorVO().getMessage().contains("kk"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }
}
