package org.appverse.web.framework.backend.frontfacade.rest;

import static org.junit.Assert.assertEquals;

import javax.annotation.PostConstruct;

import org.appverse.web.framework.backend.frontfacade.rest.TestExceptionHandlingResource.SimpleBean;
import org.appverse.web.framework.backend.frontfacade.rest.beans.ResponseDataVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {FrontFacadeModuleTestsConfigurationApplication.class})
@WebIntegrationTest(randomPort= true, value={"security.basic.enabled=false","server.servlet-path=/spring"})
public class JerseyExceptionHandlerTests {
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
    	String s = restTemplate.getForEntity("http://localhost:"+port+"/hello", String.class).getBody();
        assertEquals("Hello World!", s);
    }
    
    /**
     * Test if the response comes from JerseyExceptionHandler.
     */
    @Test
    public void testExceptionHandler() {
    	int port = context.getEmbeddedServletContainer().getPort();
    	ResponseDataVO s = restTemplate.getForEntity("http://localhost:"+port+"/exc", ResponseDataVO.class).getBody();
    	assertEquals(500, s.getErrorVO().getCode());
    }
}
