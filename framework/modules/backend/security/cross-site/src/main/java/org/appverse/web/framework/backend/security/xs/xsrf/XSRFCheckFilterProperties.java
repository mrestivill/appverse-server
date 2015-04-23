package org.appverse.web.framework.backend.security.xs.xsrf;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for configuring {@link XSRFCheckFilter}.
 */
@ConfigurationProperties(prefix = "appverse.security.xs.xsrffilter")
public class XSRFCheckFilterProperties {
	
	private final Map<String, String> initParameters = new HashMap<String, String>();

	private Boolean wildcards;
	
	private String match;
	
	private String exclude;
	
	private String getXsrfPath;
	
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

	public String getGetXsrfPath() {
		return getXsrfPath;
	}

	public void setGetXsrfPath(String getXsrfPath) {
		this.addInitParameter("getXsrfPath", getXsrfPath);
		this.getXsrfPath = getXsrfPath;
	}
	
	Map<String, String> getAsInitParameters() {
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
	
	
/*
	private final Map<String, String> initParameters = new HashMap<String, String>();

	public XSRFCheckFilterProperties() {
		this.addInitParameter("checkGzExists", false);
	}

	public Integer getBufferSize() {
		return this.bufferSize;
	}

	public void setBufferSize(Integer bufferSize) {
		this.addInitParameter("bufferSize", bufferSize);
		this.bufferSize = bufferSize;
	}

	public Integer getMinGzipSize() {
		return this.minGzipSize;
	}

	public void setMinGzipSize(Integer minGzipSize) {
		this.addInitParameter("minGzipSize", minGzipSize);
		this.minGzipSize = minGzipSize;
	}

	public Integer getDeflateCompressionLevel() {
		return this.deflateCompressionLevel;
	}

	public void setDeflateCompressionLevel(Integer deflateCompressionLevel) {
		this.addInitParameter("deflateCompressionLevel", deflateCompressionLevel);
		this.deflateCompressionLevel = deflateCompressionLevel;
	}

	public Boolean getDeflateNoWrap() {
		return this.deflateNoWrap;
	}

	public void setDeflateNoWrap(Boolean deflateNoWrap) {
		this.addInitParameter("deflateNoWrap", deflateNoWrap);
		this.deflateNoWrap = deflateNoWrap;
	}

	public List<HttpMethod> getMethods() {
		return this.methods;
	}

	public void setMethods(List<HttpMethod> methods) {
		this.addInitParameter("methods",
				StringUtils.collectionToCommaDelimitedString(methods));
		this.methods = methods;
	}

	public List<MimeType> getMimeTypes() {
		return this.mimeTypes;
	}

	public void setMimeTypes(List<MimeType> mimeTypes) {
		this.addInitParameter("mimeTypes",
				StringUtils.collectionToCommaDelimitedString(mimeTypes));
		this.mimeTypes = mimeTypes;
	}

	public String getExcludedAgents() {
		return this.excludedAgents;
	}

	public void setExcludedAgents(String excludedAgents) {
		this.addInitParameter("excludedAgents", excludedAgents);
		this.excludedAgents = excludedAgents;
	}

	public String getExcludedAgentPatterns() {
		return this.excludedAgentPatterns;
	}

	public void setExcludedAgentPatterns(String excludedAgentPatterns) {
		this.addInitParameter("excludedAgentPatterns", excludedAgentPatterns);
		this.excludedAgentPatterns = excludedAgentPatterns;
	}

	public String getExcludedPaths() {
		return this.excludedPaths;
	}

	public void setExcludedPaths(String excludedPaths) {
		this.addInitParameter("excludedPaths", excludedPaths);
		this.excludedPaths = excludedPaths;
	}

	public String getExcludedPathPatterns() {
		return this.excludedPathPatterns;
	}

	public void setExcludedPathPatterns(String excludedPathPatterns) {
		this.addInitParameter("excludedPathPatterns", excludedPathPatterns);
		this.excludedPathPatterns = excludedPathPatterns;
	}

	public String getVary() {
		return this.vary;
	}

	public void setVary(String vary) {
		this.addInitParameter("vary", vary);
		this.vary = vary;
	}

	Map<String, String> getAsInitParameters() {
		return this.initParameters;
	}

	private void addInitParameter(String name, Integer value) {
		if (value != null) {
			this.initParameters.put(name, value.toString());
		}
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
*/
}
