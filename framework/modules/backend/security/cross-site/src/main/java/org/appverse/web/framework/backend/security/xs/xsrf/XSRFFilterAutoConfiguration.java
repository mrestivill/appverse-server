package org.appverse.web.framework.backend.security.xs.xsrf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link XSRFCheckFilter}.
 */
@Configuration
@ConditionalOnClass(XSRFCheckFilter.class)
@EnableConfigurationProperties(XSRFCheckFilterProperties.class)
public class XSRFFilterAutoConfiguration {
	
	private String DEFAULT_URL_PATTERN = "/rest/*";
	private String DEFAULT_MATCH = "*";
	private String DEFAULT_EXCLUDE = "";
	private Boolean DEFAULT_WILCARDS = Boolean.FALSE;
	private String DEFAULT_GET_XSRF_PATH = "getXSRFSessionToken";
	
	/*
	 *     
	 <filter>
        <filter-name>XSRFFilter</filter-name>
        <filter-class>org.appverse.web.framework.backend.api.helpers.security.XSRFCheckFilter</filter-class>
             <init-param>
                <param-name>match</param-name>
                <param-value>*</param-value>
            </init-param>
            <init-param>
            	<param-name>wildcards</param-name>
            	<param-value>false</param-value>
            </init-param>
            <init-param>
                <param-name>getXsrfPath</param-name>
                <param-value>getXSRFSessionToken</param-value>
            </init-param>
    </filter>
    <filter-mapping>
    	<filter-name>XSRFFilter</filter-name>
    	<url-pattern>/admin/rest/*</url-pattern>
    </filter-mapping>
	*/
	
	
	

	@Autowired
	private XSRFCheckFilterProperties properties;

	@Bean
	public FilterRegistrationBean gzipFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean(new XSRFCheckFilter());
		
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
