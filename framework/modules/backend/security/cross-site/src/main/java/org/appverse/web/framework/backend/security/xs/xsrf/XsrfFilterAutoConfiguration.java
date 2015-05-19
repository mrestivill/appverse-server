package org.appverse.web.framework.backend.security.xs.xsrf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link XsrfFilter}.
 */
@Configuration
@ConditionalOnClass(XsrfFilter.class)
@EnableConfigurationProperties(XsrfFilterProperties.class)
public class XsrfFilterAutoConfiguration {
	
	private String DEFAULT_URL_PATTERN = "/api/*";
	private String DEFAULT_MATCH = "*";
	private String DEFAULT_EXCLUDE = null;
	private Boolean DEFAULT_WILCARDS = Boolean.FALSE;
	private String DEFAULT_GET_XSRF_PATH = "getXSRFSessionToken";
	
	@Autowired
	private XsrfFilterProperties properties;

	@Bean
	public FilterRegistrationBean xsrfFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean(new XsrfFilter());
		
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
		if (properties.getGetXsrfPath() == null ||  properties.getGetXsrfPath().isEmpty()){
			properties.setGetXsrfPath(DEFAULT_GET_XSRF_PATH);
		}				
		registration.addUrlPatterns(properties.getUrlPattern());
		registration.setInitParameters(this.properties.getAsInitParameters());
		return registration;
	}

}
