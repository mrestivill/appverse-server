package org.appverse.web.framework.backend.security.xs.xss;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link XSSSecurityFilter}.
 */
@Configuration
@ConditionalOnClass(XSSSecurityFilter.class)
public class XSSSecurityFilterAutoConfiguration {
	
	@Bean
	public FilterRegistrationBean xssSecurityFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean(new XSSSecurityFilter());
		return registration;
	}
}
