package org.appverse.web.framework.backend.frontfacade.rest;

import org.appverse.web.framework.backend.core.services.AbstractPresentationService;
import org.appverse.web.framework.backend.frontfacade.rest.beans.ResponseDataVO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${appverse.frontfacade.rest.api.basepath:/api}", method = RequestMethod.POST)
public class TestExceptionHandlingResource extends AbstractPresentationService{

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String getHello() {
        return "Hello World!";
    }
    
    @RequestMapping(value="/exc",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public SimpleBean testExc() throws Exception {
    	System.out.println("test");
    	if( true) {
    		throw new Exception("kk");
    	}
    	return null;
    }
    
    public class SimpleBean extends ResponseDataVO {
		private static final long serialVersionUID = 3762709404320890375L;
    }
}
