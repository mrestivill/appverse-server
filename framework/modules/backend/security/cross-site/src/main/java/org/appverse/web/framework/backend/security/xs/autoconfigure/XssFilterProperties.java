package org.appverse.web.framework.backend.security.xs.autoconfigure;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Properties for configuring {@link XsrfFilter}.
 * urlPattern: the filter URL pattern
 * match: comma separated string patterns to be found in the uri for using XSRFCheckFilter. Only uris that match these patterns will be checked. Use '*' to enable default matching.
 * widlcards {true|false}  boolean that specifies wildcard matching for string patterns. by default false. 
 * exludes: comma separated string patterns to be excluded if found in uri for using xss filter. It is applied only if all urls are matched.
 */
@ConfigurationProperties(prefix = "appverse.security.xs.xss.filter")
@EnableConfigurationProperties(XssFilterProperties.class)
public class XssFilterProperties {
	
	private final Map<String, String> initParameters = new HashMap<String, String>();

	private Boolean wildcards;
	
	private String match;
	
	private String exclude;
		
	private String urlPattern;
	
	public Boolean getWildcards() {
		return wildcards;
	}

	public void setWildcards(Boolean wildcards) {
		this.addInitParameter("wildcards", wildcards);
		this.wildcards = wildcards;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.addInitParameter("match", match);
		this.match = match;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.addInitParameter("exclude", exclude);
		this.exclude = exclude;
	}

	public Map<String, String> getAsInitParameters() {
		return this.initParameters;
	}
	
	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.addInitParameter("urlPattern", urlPattern);
		this.urlPattern = urlPattern;
	}

	private void addInitParameter(String name, Boolean value) {
		if (value != null) {
			this.initParameters.put(name, value.toString());
		}
	}

	private void addInitParameter(String name, String value) {
		if (value != null) {
			this.initParameters.put(name, value.toString());
		}
	}	
}
