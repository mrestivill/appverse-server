package org.appverse.web.framework.backend.security.xs.autoconfigure;

import org.appverse.web.framework.backend.security.xs.xsrf.XsrfFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link XsrfFilter}.
 */
@Configuration
@ConditionalOnClass(XsrfFilter.class)
@ConditionalOnProperty(value="appverse.security.xs.xsrf.filter.enabled", matchIfMissing=true)
@EnableConfigurationProperties(XsrfFilterProperties.class)
public class XsrfFilterAutoConfiguration {
	
	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	private String baseApiPath;
	
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
			properties.setUrlPattern(baseApiPath + "/*");
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
		registration.setInitParameters(properties.getAsInitParameters());
		return registration;
	}
}