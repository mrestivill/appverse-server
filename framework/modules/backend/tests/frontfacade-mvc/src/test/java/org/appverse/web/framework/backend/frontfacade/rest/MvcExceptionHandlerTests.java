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
package org.appverse.web.framework.backend.frontfacade.rest;

import org.appverse.web.framework.backend.frontfacade.rest.beans.ResponseDataVO;
import org.appverse.web.framework.backend.test.util.frontfacade.BaseAbstractAuthenticationRequiredTest;
import org.appverse.web.framework.backend.test.util.frontfacade.mvc.tests.predefined.TestLoginInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {FrontFacadeModuleTestsConfigurationApplication.class})
@WebIntegrationTest(randomPort= true)
public class MvcExceptionHandlerTests extends BaseAbstractAuthenticationRequiredTest{
	 
    @Test
    public void test() throws Exception {
    	// Login first
    	TestLoginInfo loginInfo = login();
    	HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", loginInfo.getJsessionid());
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// Calling protected resource - requires CSRF token
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + "/hello")
				.queryParam(DEFAULT_CSRF_PARAMETER_NAME, loginInfo.getXsrfToken());
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);
    	
        String data = responseEntity.getBody();
        assertEquals("Hello World!", data);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testNotFound() throws Exception {
    	// Login first
    	TestLoginInfo loginInfo = login();
    	HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", loginInfo.getJsessionid());
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		
		// Calling protected resource - requires CSRF token
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + "/badurl")
				.queryParam(DEFAULT_CSRF_PARAMETER_NAME, loginInfo.getXsrfToken());
    	
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
    
    @Test
    public void testExceptionHandler() throws Exception {
    	// Login first
    	TestLoginInfo loginInfo = login();
    	HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", loginInfo.getJsessionid());
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		// Calling protected resource - requires CSRF token
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + baseApiPath + "/exc")
				.queryParam(DEFAULT_CSRF_PARAMETER_NAME, loginInfo.getXsrfToken());
		
		ResponseEntity<ResponseDataVO> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, ResponseDataVO.class);
    	
        ResponseDataVO data = responseEntity.getBody();
        assertEquals(500, data.getErrorVO().getCode());
        assertTrue("contains message", data.getErrorVO().getMessage().contains("kk"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

    }

	@Override
	protected String getPassword() {
		return "password";
	}

	@Override
	protected String getUsername() {
		return"user";
	}

	@Override
	protected String getAnUserRole() {
		return "ROLE_USER";
	}			
}
