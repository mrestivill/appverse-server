package org.appverse.web.framework.backend.frontfacade.rest.handler;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.appverse.web.framework.backend.core.exceptions.IntegrationException;
import org.appverse.web.framework.backend.frontfacade.rest.beans.ErrorVO;
import org.appverse.web.framework.backend.frontfacade.rest.beans.ResponseDataVO;
import org.springframework.stereotype.Repository;

@Provider
@Repository
//TODO autoconfiguration.... enabled:true/false (register or not) - and relate to the FrontFacadeRestAutoConfiguration 
//TODO provide default implementation that might be extender by the project.
public class JerseyExceptionHandler implements ExceptionMapper<Exception> {

	public Response toResponse(Exception exception) {
		/**
		 * This is a first, basic, implementation, just capturing the Exception and translating to Code 500 and propagating the exception message
		 */
		ErrorVO error = new ErrorVO();
		ResponseDataVO data = new ResponseDataVO();
		data.setErrorVO(error);
		error.setCode(500L);
		error.setMessage(exception.getMessage());

		/**
		 * TODO Might be interesting the Status Code could be configurable/override by configuration
		 */
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(data).build();
	}

}
