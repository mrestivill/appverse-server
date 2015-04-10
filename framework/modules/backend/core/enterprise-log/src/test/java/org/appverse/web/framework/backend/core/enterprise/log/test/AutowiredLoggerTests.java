package org.appverse.web.framework.backend.core.enterprise.log.test;

import static org.junit.Assert.assertNotNull;

import org.appverse.web.framework.backend.core.enterprise.log.AutowiredLogger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=AutowiredLoggerTestsApplication.class)
public class AutowiredLoggerTests {
	
	@AutowiredLogger
	private static Logger logger;	
	
	@Test
	public void remoteLogServiceTest() {
		assertNotNull(logger);		
		logger.debug("AutowiredLoggerTests test menssage");
	}

}
