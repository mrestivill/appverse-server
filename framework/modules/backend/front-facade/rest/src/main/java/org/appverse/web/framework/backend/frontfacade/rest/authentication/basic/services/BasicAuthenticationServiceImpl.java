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

package org.appverse.web.framework.backend.frontfacade.rest.authentication.basic.services;

import org.appverse.web.framework.backend.security.authentication.userpassword.managers.UserAndPasswordAuthenticationManager;
import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;
import org.appverse.web.framework.backend.security.xs.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Singleton
@Path("/sec")
/**
 * {@link BasicAuthenticationService} based on JAX-RS (Jersey) implmentation. Exposes a Basic Autentication service for REST services 
 * providing a "login" service.
 * The controller obtains "Authorization" header, accordding to Basic Authentication, from the request in order to obtain
 * username and password and delegates in a service that authenticates the user.
 * The controller creates an HttpSession so that the user is already authenticated in successive requests and adds a
 * "JSESSIONID" cookie to the response.
 */
public class BasicAuthenticationServiceImpl implements BasicAuthenticationService {

    @Autowired
	private UserAndPasswordAuthenticationManager userAndPasswordAuthenticationManager;

    /**
     * Authenticates an user. Requires basic authentication header.
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     * @throws Exception
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("login")
    public Response login(@Context HttpServletRequest httpServletRequest,
                                     @Context HttpServletResponse httpServletResponse) throws Exception {

        String[] userNameAndPassword;

        // Invalidate session if exists
        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession != null) httpSession.invalidate();

        try{
            userNameAndPassword = obtainUserAndPasswordFromBasicAuthenticationHeader(httpServletRequest);
        }
        catch (BadCredentialsException e){
            httpServletResponse.addHeader("WWW-Authenticate", "Basic");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new AuthorizationData()).build();
        }

        //Create and set the cookie
        httpServletRequest.getSession(true);
        String jsessionId = httpServletRequest.getSession().getId();
        Cookie sessionIdCookie = new Cookie("JSESSIONID", jsessionId);
        httpServletResponse.addCookie(sessionIdCookie);

        // Obtain XSRFToken and add it as a response header
        String xsrfToken = SecurityHelper.createXSRFToken(httpServletRequest);
        httpServletResponse.addHeader(SecurityHelper.XSRF_TOKEN_NAME, xsrfToken);

        // Authenticate principal and return authorization data
        AuthorizationData authData = userAndPasswordAuthenticationManager.authenticatePrincipal(userNameAndPassword[0], userNameAndPassword[1]);

        // AuthorizationDataVO
        return Response.status(Response.Status.OK).entity(authData).build();
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