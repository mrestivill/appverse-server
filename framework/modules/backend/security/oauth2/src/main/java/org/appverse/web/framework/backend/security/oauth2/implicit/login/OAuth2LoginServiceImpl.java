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
package org.appverse.web.framework.backend.security.oauth2.implicit.login;

import org.appverse.web.framework.backend.security.authentication.userpassword.managers.UserAndPasswordAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@ConditionalOnProperty(value="appverse.frontfacade.rest.simpleAuthenticationEndpoint.enabled", matchIfMissing=true)
@RequestMapping(value = "${appverse.frontfacade.rest.api.basepath:/api}", method = RequestMethod.POST)
/**
 * TODO: Document here!
 */
public class OAuth2LoginServiceImpl implements OAuth2LoginService {
	
	@Value("${security.enable-csrf:true}")
	private boolean securityEnableCsrf;

    @Autowired
	private UserAndPasswordAuthenticationManager userAndPasswordAuthenticationManager;
    
    private static final String CSRF_TOKEN_SESSION_ATTRIBUTE = "org.springframework.security.web.csrf.CsrfToken";

    /**
     * Authenticates an user. Requires basic authentication header.
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "${appverse.oauth2.implicitflow.loginEndpoint.path:/oauth/login}", method = RequestMethod.POST)
    public void login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
    	if (username == null || password == null) {
    		throw new BadCredentialsException("username or password is null");
    	}
    	// Authenticate principal and return authorization data
    	userAndPasswordAuthenticationManager.authenticatePrincipal(username, password);

/* TODO: See how CSRF fits here. Are we going to use CSRF with OAuth2?
    	if (securityEnableCsrf){
    		// Obtain XSRFToken and add it as a response header
    		// The token comes in the request (CsrFilter adds it) and we need to set it in the response so the clients 
    		// have it to use it in the next requests
    		CsrfToken csrfToken  = (CsrfToken) httpServletRequest.getAttribute(CSRF_TOKEN_SESSION_ATTRIBUTE);
    		httpServletResponse.addHeader(csrfToken.getHeaderName(), csrfToken.getToken());
    	}
    	
    	In AppverseWebBasicAuthenticationConfigurerAdapter is handled like this:
		
		if (securityEnableCsrf){
			http.csrf()
			.requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher());
		}
		else http.csrf().disable();
*/    	

    	RequestDispatcher dispatcher = httpServletRequest.getRequestDispatcher("/oauth/authorize");
    	dispatcher.forward(httpServletRequest, httpServletResponse);        	
   }
}