package org.appverse.web.framework.backend.security.xs.autoconfigure;

import org.appverse.web.framework.backend.security.xs.xss.XssFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link XssFilter}.
 */
@Configuration
@ConditionalOnClass(XssFilter.class)
@ConditionalOnProperty(value="appverse.security.xs.xss.filter.enabled", matchIfMissing=true)
@EnableConfigurationProperties(XssFilterProperties.class)
public class XssFilterAutoConfiguration {
	
	private String DEFAULT_URL_PATTERN = "/*";
	private String DEFAULT_MATCH = "*";
	private String DEFAULT_EXCLUDE = null;
	private Boolean DEFAULT_WILCARDS = Boolean.FALSE;
	
	@Autowired
	private XssFilterProperties properties;	
	
	@Bean
	public FilterRegistrationBean xssFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean(new XssFilter());

		// Default values if not set
		if (properties.getUrlPattern() == null ||  properties.getUrlPattern().isEmpty()){
			properties.setUrlPattern(DEFAULT_URL_PATTERN);
		}
		if (properties.getMatch() == null ||  properties.getMatch().isEmpty()){
			properties.setMatch(DEFAULT_MATCH);
		}
		if (properties.getExclude() == null ||  properties.getExclude().isEmpty()){
			properties.setExclude(DEFAULT_EXCLUDE);
		}
		if (properties.getWildcards() == null){
			properties.setWildcards(DEFAULT_WILCARDS);
		}
		registration.addUrlPatterns(properties.getUrlPattern());
		registration.setInitParameters(properties.getAsInitParameters());
		return registration;
	}
}