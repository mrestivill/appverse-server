package org.appverse.web.framework.backend.core.enterprise.log.autoconfigure;

import org.appverse.web.framework.backend.core.enterprise.log.AutowiredLogger;
import org.appverse.web.framework.backend.core.enterprise.log.AutowiredLoggerBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Autoconfiguration class for AutowiredLogger annotation.
 * You will be able to use AutowiredLogger annotation only if you have auto-configuration enabled or 
 * you specify you want to use this configuration explicitly.
 */
@Configuration
@ConditionalOnClass(AutowiredLogger.class)
public class AutowiredLoggerAutoConfiguration {
	
	@Bean
	public AutowiredLoggerBeanPostProcessor autowiredLoggerBeanPostProcessor() {
		return new AutowiredLoggerBeanPostProcessor();
	}
}