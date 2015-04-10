package org.appverse.web.framework.backend.core.enterprise.log.autoconfigure;

import org.appverse.web.framework.backend.core.enterprise.log.AutowiredLogger;
import org.appverse.web.framework.backend.core.enterprise.log.AutowiredLoggerBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(AutowiredLogger.class)
// @AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
// @ConditionalOnBean(JobLauncher.class)
// @EnableConfigurationProperties(BatchProperties.class)
public class AutowiredLoggerAutoConfiguration {
	
	@Bean
	public AutowiredLoggerBeanPostProcessor autowiredLoggerBeanPostProcessor() {
		return new AutowiredLoggerBeanPostProcessor();
	}
}

/*
@Configuration
@EnableConfigurationProperties
protected static class Config {

	protected static MockEmbeddedServletContainerFactory containerFactory = null;

	@Bean
	public EmbeddedServletContainerFactory containerFactory() {
		if (containerFactory == null) {
			containerFactory = new MockEmbeddedServletContainerFactory();
		}
		return containerFactory;
	}

	@Bean
	public EmbeddedServletContainerCustomizerBeanPostProcessor embeddedServletContainerCustomizerBeanPostProcessor() {
		return new EmbeddedServletContainerCustomizerBeanPostProcessor();
	}

}
*/