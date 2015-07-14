/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0�?). If a copy of the APL was not distributed with this 
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
package org.appverse.web.framework.backend.test.util.frontfacade.xs.xss.tests.predefined;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.test.OutputCapture;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * This abstract test class provides some tests for possible XSS attacks that OWASP ESAPI library prevents from happening.
 * You can extend this class in your project to have this predefined tests with your own configuration and to add your own.
 * The only thing you need to take into account is that we have disabled CSRF to make the tests simpler.
 * 
 * Necessary to have httpclient for this tests (with scope tests) so that the http returns are handle correctly.
 * Otherwise you will experience exactly the problem described here:
 * http://stackoverflow.com/questions/27341604/exception-when-using-testresttemplate 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
@WebIntegrationTest(randomPort= true, 
					value={"security.enable-csrf=false"})
public abstract class XssFilterPredefinedTests {
	
	@Autowired
	private AnnotationConfigEmbeddedWebApplicationContext context;
	
	@Rule
	public OutputCapture capture = new OutputCapture();	
	
	// Be Careful!!! TestRestTemplate ignores cookies
	RestTemplate restTemplate = new RestTemplate();	
	
	@Configuration
	@EnableAutoConfiguration
	@RestController
	public static class ApplicationTest {

		public static void main(String[] args) {
			SpringApplication.run(ApplicationTest.class, args);
		}

		@RequestMapping(value="/test", method = RequestMethod.POST)
		public void test(@RequestHeader("testRiskyHeader") String testRiskyHeader,
						 @RequestHeader("testSafeHeader") String testSafeHeader,
				         @RequestBody String requestBody){
			System.out.println(testRiskyHeader);
			System.out.println(testSafeHeader);
			System.out.println(requestBody);			
		}
		
		@RequestMapping(value="/test/{testSafeUrlpath}/{testRiskyUrlpath}", method = RequestMethod.GET)
		public String test2(@RequestHeader("testSafeHeader") String testSafeHeader,
				          @PathVariable String testSafeUrlpath,
				          @PathVariable String testRiskyUrlpath){
			System.out.println(testSafeHeader);
			System.out.println(testSafeUrlpath);
			System.out.println(testRiskyUrlpath);
			return "";
		}
		
		@RequestMapping(value="/test/{testSafeUrlpath}", method = RequestMethod.GET)
		public String test3(@RequestHeader("testSafeHeader") String testSafeHeader,
				          @PathVariable String testSafeUrlpath,
				          @RequestParam String safeQueryStringParam,
				          @RequestParam String riskyQueryStringParam){
			System.out.println(testSafeHeader);
			System.out.println(testSafeUrlpath);
			System.out.println(safeQueryStringParam);
			System.out.println(riskyQueryStringParam);
			return "";
		}
		
		@RequestMapping(value="/test", method = RequestMethod.GET)
		public String test4(@CookieValue(value="safeCookie") String safeCookie,
							@CookieValue(value="riskyCookie", required=false) String riskyCookie){
			System.out.println(safeCookie);
			System.out.println(riskyCookie);
			return "";
		}				
	}
	
	
	
	@Test
	public void testInitParameterConfiguration() {
		FilterRegistrationBean registrationBean = this.context.getBean("xssFilter",
				FilterRegistrationBean.class);
		assertThat(registrationBean.getInitParameters().size(), equalTo(3));
		assertThat(registrationBean.getInitParameters().get("urlPattern"),
				equalTo("/*"));		
		assertThat(registrationBean.getInitParameters().get("match"),
				equalTo("*"));
		assertThat(registrationBean.getInitParameters().get("wildcards"),
				equalTo("false"));
		assertThat(registrationBean.getInitParameters().get("exclude"),
				equalTo(null));
	}
	
	@Test
	public void testXssFilterInHeader() throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.set("testRiskyHeader", "<script>alert(document.cookie);</script>");
		headers.set("testSafeHeader", "safeHeader");
		HttpEntity<String> entity = new HttpEntity<String>("testParamInBody1=testvalue1", headers);
		
		int port = context.getEmbeddedServletContainer().getPort();
		restTemplate.postForEntity("http://localhost:" + port + "/test", entity, String.class);		
		// Risky header has been skipped
		String captureString = capture.toString();
		assertThat(captureString, not(containsString("<script>alert(document.cookie);</script>")));
		assertThat(captureString, containsString(""));
		// Safe header is kept
		assertThat(capture.toString(), containsString("safeHeader"));
		// Safe body is kept
		assertThat(capture.toString(), containsString("testParamInBody1=testvalue1"));
	}
	
	@Test
	public void testXssFilterInCookie() throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie","safeCookie=safeCookie,riskyCookie=<script>alert(document.cookie);</script>");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		int port = context.getEmbeddedServletContainer().getPort();
		restTemplate.exchange("http://localhost:" + port + "/test", HttpMethod.GET, entity, String.class);
		// Safe cookie has been kept
		assertThat(capture.toString(), containsString("safeCookie"));
		// Risky cookie has been skipped
		assertThat(capture.toString(), not(containsString("<script>alert(document.cookie);</script>")));
	}
	
	@Test
	public void testXssFilterInUrlQueryString() throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.set("testSafeHeader", "safeHeader");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		int port = context.getEmbeddedServletContainer().getPort();
		restTemplate.exchange("http://localhost:" + port + "/test/safeurlpath?safeQueryStringParam=safeQueryStringParam&riskyQueryStringParam=<script>alert(document.cookie);</script>", HttpMethod.GET, entity, String.class);
		// Safe header is kept
		assertThat(capture.toString(), containsString("safeHeader"));
		// Safe url path is kept
		assertThat(capture.toString(), containsString("safeurlpath"));
		// Safe query string parameter is kept
		assertThat(capture.toString(), containsString("safeQueryStringParam"));	
		// Risky query string parameter has been skipped
		assertThat(capture.toString(), not(containsString("<script>alert(document.cookie);</script>")));				
	}
	
	
	@Test
	public void testXssFilterInUrlPath() throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.set("testSafeHeader", "safeHeader");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		int port = context.getEmbeddedServletContainer().getPort();
		restTemplate.exchange("http://localhost:" + port + "/test/safeurlpath/document.cookie", HttpMethod.GET, entity, String.class);
		// Safe header is kept
		assertThat(capture.toString(), containsString("safeHeader"));
		// Safe url path is kept
		assertThat(capture.toString(), containsString("safeurlpath"));
		// Risky URK path is skipped
		assertThat(capture.toString(), not(containsString("document.cookie")));
		
	}
}
