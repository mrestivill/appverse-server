package org.appverse.web.framework.backend.frontfacade.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.appverse.web.framework.backend.core.services.AbstractPresentationService;
import org.appverse.web.framework.backend.frontfacade.rest.beans.ResponseDataVO;

@Path("/")
public class TestExceptionHandlingResource extends AbstractPresentationService{
    @GET
    @Path("/hello")
    public String getHello() {
        return "Hello World!";
    }
    
    @GET
    @Path("/exc")
	@Produces(MediaType.APPLICATION_JSON)
    public SimpleBean testExc() throws Exception {
    	System.out.println("test");
    	if( true) {
    		throw new Exception("kk");
    	}
    	return null;
    }
    
    public class SimpleBean extends ResponseDataVO {
    }
}
