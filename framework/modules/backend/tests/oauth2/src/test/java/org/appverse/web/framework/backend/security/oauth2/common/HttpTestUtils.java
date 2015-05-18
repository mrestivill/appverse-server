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
package org.appverse.web.framework.backend.security.oauth2.common;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.test.RestTemplateHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/* TODO
 * Take into account that this common test classes are taken from:
 * https://github.com/spring-projects/spring-security-oauth/tree/master/tests/annotation/jdbc
 * by Dave Syer.
 * In this test project there are not license headers (maybe the same license that applies to the complete
 * spring-oauth2 project?). Before making this public we need to clarify (a good way would be to ask Dave 
 * directly) if it is possible to copy here this code and use it as a common class for our tests as he 
 * does in his example.
 * It would have been ideal to directly add a test dependency directly including the artifact that has the
 * example to use the classes for tests but the problem is that they seem not to be published.
 * Not even in the spring repositories that are enabled when you run the example with -Bootstrap profile
 * IMPORTANT!!! We need to clarify this before publishing anything.
 */
/**
 * <p>
 * A rule that provides HTTP connectivity to test cases on the assumption that the server is available when test methods
 * fire.
 * </p>
 */
public class HttpTestUtils implements MethodRule, RestTemplateHolder {

	private static int DEFAULT_PORT = 8080;

	private static String DEFAULT_HOST = "localhost";

	private int port;

	private String hostName = DEFAULT_HOST;

	private RestOperations client;

	private String prefix = "";

	/**
	 * @return a new rule that sets up default host and port etc.
	 */
	public static HttpTestUtils standard() {
		return new HttpTestUtils();
	}

	private HttpTestUtils() {
		setPort(DEFAULT_PORT);
	}

	/**
	 * @param prefix
	 */
	public void setPrefix(String prefix) {
		if (!StringUtils.hasText(prefix)) {
			prefix = "";
		} else while (prefix.endsWith("/")) {
			prefix = prefix.substring(0, prefix.lastIndexOf("/"));
		}
		this.prefix = prefix;
	}

	/**
	 * @param port the port to set
	 */
	public HttpTestUtils setPort(int port) {
		this.port = port;
		if (client == null) {
			client = createRestTemplate();
		}
		return this;
	}

	/**
	 * @param hostName the hostName to set
	 */
	public HttpTestUtils setHostName(String hostName) {
		this.hostName = hostName;
		return this;
	}

	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				base.evaluate();
			}
		};

	}

	public String getBaseUrl() {
		return "http://" + hostName + ":" + port + prefix;
	}

	public String getUrl(String path) {
		if (path.startsWith("http")) {
			return path;
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return "http://" + hostName + ":" + port + prefix + path;
	}

	public ResponseEntity<String> postForString(String path, MultiValueMap<String, String> formData) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return client.exchange(getUrl(path), HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData,
				headers), String.class);
	}

	public ResponseEntity<String> postForString(String path, HttpHeaders headers, MultiValueMap<String, String> formData) {
		HttpHeaders actualHeaders = new HttpHeaders();
		actualHeaders.putAll(headers);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return client.exchange(getUrl(path), HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData,
				actualHeaders), String.class);
	}

	@SuppressWarnings("rawtypes")
	public ResponseEntity<Map> postForMap(String path, MultiValueMap<String, String> formData) {
		return postForMap(path, new HttpHeaders(), formData);
	}

	@SuppressWarnings("rawtypes")
	public ResponseEntity<Map> postForMap(String path, HttpHeaders headers, MultiValueMap<String, String> formData) {
		if (headers.getContentType() == null) {
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		}
		return client.exchange(getUrl(path), HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData,
				headers), Map.class);
	}

	public ResponseEntity<String> postForStatus(String path, MultiValueMap<String, String> formData) {
		return postForStatus(this.client, path, formData);
	}

	public ResponseEntity<String> postForStatus(String path, HttpHeaders headers, MultiValueMap<String, String> formData) {
		return postForStatus(this.client, path, headers, formData);
	}

	private ResponseEntity<String> postForStatus(RestOperations client, String path,
			MultiValueMap<String, String> formData) {
		return postForStatus(client, path, new HttpHeaders(), formData);
	}

	private ResponseEntity<String> postForStatus(RestOperations client, String path, HttpHeaders headers,
			MultiValueMap<String, String> formData) {
		HttpHeaders actualHeaders = new HttpHeaders();
		actualHeaders.putAll(headers);
		actualHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return client.exchange(getUrl(path), HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData,
				actualHeaders), String.class);
	}

	public ResponseEntity<String> postForRedirect(String path, HttpHeaders headers, MultiValueMap<String, String> params) {
		ResponseEntity<String> exchange = postForStatus(path, headers, params);

		if (exchange.getStatusCode() != HttpStatus.FOUND) {
			throw new IllegalStateException("Expected 302 but server returned status code " + exchange.getStatusCode());
		}

		if (exchange.getHeaders().containsKey("Set-Cookie")) {
			String cookie = exchange.getHeaders().getFirst("Set-Cookie");
			headers.set("Cookie", cookie);
		}

		String location = exchange.getHeaders().getLocation().toString();

		return client.exchange(location, HttpMethod.GET, new HttpEntity<Void>(null, headers), String.class);
	}

	public ResponseEntity<String> getForString(String path) {
		return getForString(path, new HttpHeaders());
	}

	public ResponseEntity<String> getForString(String path, final HttpHeaders headers) {
		return client.exchange(getUrl(path), HttpMethod.GET, new HttpEntity<Void>((Void) null, headers), String.class);
	}

	public ResponseEntity<String> getForString(String path, final HttpHeaders headers, Map<String, String> uriVariables) {
		return client.exchange(getUrl(path), HttpMethod.GET, new HttpEntity<Void>((Void) null, headers), String.class,
				uriVariables);
	}

	public ResponseEntity<Void> getForResponse(String path, final HttpHeaders headers, Map<String, String> uriVariables) {
		HttpEntity<Void> request = new HttpEntity<Void>(null, headers);
		return client.exchange(getUrl(path), HttpMethod.GET, request, (Class<Void>) null, uriVariables);
	}

	public ResponseEntity<Void> getForResponse(String path, HttpHeaders headers) {
		return getForResponse(path, headers, Collections.<String, String> emptyMap());
	}

	public HttpStatus getStatusCode(String path, final HttpHeaders headers) {
		ResponseEntity<Void> response = getForResponse(path, headers);
		return response.getStatusCode();
	}

	public HttpStatus getStatusCode(String path) {
		return getStatusCode(getUrl(path), null);
	}

	public void setRestTemplate(RestOperations restTemplate) {
		client = restTemplate;
	}

	public RestOperations getRestTemplate() {
		return client;
	}

	public RestOperations createRestTemplate() {
		RestTemplate client = new TestRestTemplate();
		return client;
	}

	public UriBuilder buildUri(String url) {
		return UriBuilder.fromUri(url.startsWith("http:") ? url : getUrl(url));
	}

	public static class UriBuilder {

		private final String url;

		private Map<String, String> params = new LinkedHashMap<String, String>();

		public UriBuilder(String url) {
			this.url = url;
		}

		public static UriBuilder fromUri(String url) {
			return new UriBuilder(url);
		}

		public UriBuilder queryParam(String key, String value) {
			params.put(key, value);
			return this;
		}

		public String pattern() {
			StringBuilder builder = new StringBuilder();
			// try {
			builder.append(url.replace(" ", "+"));
			if (!params.isEmpty()) {
				builder.append("?");
				boolean first = true;
				for (String key : params.keySet()) {
					if (!first) {
						builder.append("&");
					}
					else {
						first = false;
					}
					String value = params.get(key);
					if (value.contains("=")) {
						value = value.replace("=", "%3D");
					}
					builder.append(key + "={" + key + "}");
				}
			}
			return builder.toString();

		}

		public Map<String, String> params() {
			return params;
		}

		public URI build() {
			return new UriTemplate(pattern()).expand(params);
		}
	}

}
