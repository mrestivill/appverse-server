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
package org.appverse.web.framework.backend.security.oauth2.authserver.login;

import org.appverse.web.framework.backend.security.authentication.userpassword.managers.UserAndPasswordAuthenticationManager;
import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@RestController
@ConditionalOnProperty(value="appverse.frontfacade.oauth2.apiprotection.enabled", matchIfMissing=true)

/**
* OAuth2LoginService implementation that shows an enpoint to authenticate the user with username and password based form
* authentitication.
*/


/*
* Replacing oauth2 endpoints with Java configuration results very complicated (with XML) is easy.
* See: http://stackoverflow.com/questions/22222966/how-to-change-spring-security-oauth2-default-token-endpoint
* That's the reason why we don't change token endpoint default URL for now.
*/
public class OAuth2LoginServiceImpl implements OAuth2LoginService {

	// private static final String CSRF_TOKEN_SESSION_ATTRIBUTE = "org.springframework.security.web.csrf.CsrfToken";

	@Value("${security.enable-csrf:true}")
	private boolean securityEnableCsrf;

	@Autowired
	private UserAndPasswordAuthenticationManager userAndPasswordAuthenticationManager;

	private static final String CSRF_TOKEN_SESSION_ATTRIBUTE = "org.springframework.security.web.csrf.CsrfToken";

	/**
	 * Authenticates an user using oauth implicit grant flow. Requires basic authentication header.
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "${appverse.frontfacade.oauth2.loginEndpoint.path:/sec/login}", method = RequestMethod.POST)
	public ResponseEntity<AuthorizationData> login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
/*
		if (username == null || password == null) {
			throw new BadCredentialsException("username or password is null");
		}
*/
		
    	if (securityEnableCsrf){
    		// Obtain XSRFToken and add it as a response header
    		// The token comes in the request (CsrFilter adds it) and we need to set it in the response so the clients 
    		// 	have it to use it in the next requests
    		CsrfToken csrfToken  = (CsrfToken) httpServletRequest.getAttribute(CSRF_TOKEN_SESSION_ATTRIBUTE);
    		httpServletResponse.addHeader(csrfToken.getHeaderName(), csrfToken.getToken());
    	}
		
        try{		
        	// Authenticate principal and return authorization data
        	AuthorizationData authData = userAndPasswordAuthenticationManager.authenticatePrincipal(username, password);
            // AuthorizationDataVO
            return new ResponseEntity<AuthorizationData>(authData, HttpStatus.OK);
        }catch (BadCredentialsException e){
            // httpServletResponse.addHeader("WWW-Authenticate", "Basic");
            return new ResponseEntity<AuthorizationData>(HttpStatus.UNAUTHORIZED);
        }
	}
	
	/* TODO: Add revocation in a separate controller 
	@RequestMapping(value = "${appverse.frontfacade.oauth2.logoutEndpoint.path:/sec/token/revoke}", method = RequestMethod.POST)
	@ConditionalOnExpression("#{tokenServices}!=null")
	public @ResponseBody void revokeToken(@RequestParam("access_token") String token, HttpServletRequest req) throws  InvalidClientException {
		if (tokenServices != null) {
			tokenServices.revokeToken(token);
		}else{
			throw new RuntimeException("There is not a ConsumerTokenServices available");
		}
	}
	*/
}