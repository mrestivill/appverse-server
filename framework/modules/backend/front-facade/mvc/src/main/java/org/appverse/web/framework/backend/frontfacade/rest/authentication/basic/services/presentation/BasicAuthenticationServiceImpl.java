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
package org.appverse.web.framework.backend.frontfacade.rest.authentication.basic.services.presentation;

import org.appverse.web.framework.backend.security.authentication.userpassword.managers.UserAndPasswordAuthenticationManager;
import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@ConditionalOnProperty(value="appverse.frontfacade.rest.basicAuthenticationEndpoint.enabled", matchIfMissing=true)
@RequestMapping(value = "${appverse.frontfacade.rest.api.basepath:/api}")
/**
 * {@link BasicAuthenticationService} Spring MVC implementation. Exposes a Basic Autentication service for REST services 
 * providing a "login" service.
 * The controller obtains "Authorization" header, accordding to Basic Authentication, from the request in order to obtain
 * username and password and delegates in a service that authenticates the user.
 * It requires Spring Security CSRF to be enabled as this authentication endpoint (login) has to provide CSRF token for 
 * the next requests.
 * Session fixation it is NOT implemented in this service directly as in most of the cases it will be used in combination 
 * with {@link AppverseBasicAuthenticationConfigurerAdapter} which provides the proper setup in order to prevent session
 * fixation attacks. If you wanted to use this service without the aforementioned auto-configuration you should implement
 * session fixation protection.
 */
public class BasicAuthenticationServiceImpl implements BasicAuthenticationService {

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
    @RequestMapping(value = "${appverse.frontfacade.rest.basicAuthenticationEndpoint.path:/sec/login}", method = RequestMethod.POST)
    public ResponseEntity<AuthorizationData> login(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        String[] userNameAndPassword;

        try{
            userNameAndPassword = obtainUserAndPasswordFromBasicAuthenticationHeader(httpServletRequest);
        }
        catch (BadCredentialsException e){
            httpServletResponse.addHeader("WWW-Authenticate", "Basic");
            return new ResponseEntity<AuthorizationData>(HttpStatus.UNAUTHORIZED);
        }

        // Obtain XSRFToken and add it as a response header
        // The token comes in the request (CsrFilter adds it) and we need to set it in the response so the clients 
        // have it to use it in the next requests
        CsrfToken csrfToken  = (CsrfToken) httpServletRequest.getAttribute(CSRF_TOKEN_SESSION_ATTRIBUTE);
        httpServletResponse.addHeader(csrfToken.getHeaderName(), csrfToken.getToken());
        
        try{
            // Authenticate principal and return authorization data
            AuthorizationData authData = userAndPasswordAuthenticationManager.authenticatePrincipal(userNameAndPassword[0], userNameAndPassword[1]);
            // AuthorizationDataVO
            return new ResponseEntity<AuthorizationData>(authData, HttpStatus.OK);
        }
        catch (AuthenticationException e){
            httpServletResponse.addHeader("WWW-Authenticate", "Basic");
            return new ResponseEntity<AuthorizationData>(HttpStatus.UNAUTHORIZED);
        }
    }


    private String[] obtainUserAndPasswordFromBasicAuthenticationHeader(HttpServletRequest httpServletRequest) throws Exception{
        // Authorization header
        String authHeader = httpServletRequest.getHeader("Authorization");

        if (authHeader == null) {
            throw new BadCredentialsException("Authorization header not found");
        }

        // Decode the authorization string
        String token;
        try{
            //Excluded "Basic " initial string
            byte[] decoded = Base64.decode(authHeader.substring(6).getBytes());
            token = new String(decoded);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        int separator = token.indexOf(":");

        if (separator == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[] {token.substring(0, separator), token.substring(separator + 1)};
    }           
}