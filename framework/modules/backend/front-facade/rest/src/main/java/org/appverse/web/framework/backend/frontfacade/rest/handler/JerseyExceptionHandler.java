package org.appverse.web.framework.backend.frontfacade.rest.handler;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.appverse.web.framework.backend.core.AbstractException;
import org.appverse.web.framework.backend.frontfacade.rest.beans.ErrorVO;
import org.appverse.web.framework.backend.frontfacade.rest.beans.ResponseDataVO;

//@Provider
//@Repository
//TODO autoconfiguration.... enabled:true/false (register or not) - and relate to the FrontFacadeRestAutoConfiguration 
//TODO provide default implementation that might be extended by the project.
public class JerseyExceptionHandler implements ExceptionMapper<Exception> {

	@Produces(MediaType.APPLICATION_JSON)
	public Response toResponse(Exception exception) {
		System.out.println("Jersey Exception Handler captured error ["
				+ exception.getMessage() + "]:" + exception.getClass());
		/**
		 * This is a first, basic, implementation, just capturing the Exception
		 * and translating to Code 500 and propagating the exception message
		 */
		ErrorVO error = new ErrorVO();
		ResponseDataVO data = new ResponseDataVO();
		data.setErrorVO(error);
		long code = 500L;
		Response.Status statusResponse = Status.INTERNAL_SERVER_ERROR; // DEFAULT
		if (exception instanceof NotFoundException) {
			statusResponse = Status.NOT_FOUND;
			code = statusResponse.getStatusCode();
		}  else if(exception instanceof AbstractException) {
			if (((AbstractException) exception).getCode() != null) {
				code = ((AbstractException) exception).getCode();
			}
		}
		error.setCode(code);
		error.setMessage(exception.getMessage());

		/**
		 * TODO Might be interesting the Status Code could be
		 * configurable/override by configuration
		 */
		return Response.status(statusResponse).header("Content-Type", "application/json").entity(data).build();
	}

}
