/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0”). If a copy of the APL was not distributed with this 
 file, You can obtain one at http://www.appverse.mobi/licenses/apl_v2.0.pdf. [^]

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the conditions of the AppVerse Public License v2.0 
 are met.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. EXCEPT IN CASE OF WILLFUL MISCONDUCT OR GROSS NEGLIGENCE, IN NO EVENT
 SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) 
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.
 */
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
