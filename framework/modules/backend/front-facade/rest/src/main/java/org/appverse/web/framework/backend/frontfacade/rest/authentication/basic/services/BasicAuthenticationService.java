package org.appverse.web.framework.backend.frontfacade.rest.authentication.basic.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

/**
 * Basic Authentication REST service 
 */
public interface BasicAuthenticationService {
	
    Response login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;
}
