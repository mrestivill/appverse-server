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
//TODO autoconfiguration.... enabled:true/false (register or not)
//
public class JerseyExceptionHandler implements ExceptionMapper<Exception> {

	public Response toResponse(Exception exception) {
		ErrorVO error = new ErrorVO();
		ResponseDataVO data = new ResponseDataVO();
		data.setErrorVO(error);
		if( exception instanceof IntegrationException) {
			error.setCode(501L);
			error.setMessage(exception.getMessage());
		} else {
			error.setCode(500L);
			error.setMessage(exception.getMessage());
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(data).build();
	}

}
