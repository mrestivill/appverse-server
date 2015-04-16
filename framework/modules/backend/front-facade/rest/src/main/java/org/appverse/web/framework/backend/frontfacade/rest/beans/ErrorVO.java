package org.appverse.web.framework.backend.frontfacade.rest.beans;

import org.appverse.web.framework.backend.core.beans.AbstractPresentationBean;

public class ErrorVO extends AbstractPresentationBean {
    /**
     * Error Code for error handling. 0 for success.
     */
    private long code;

    /**
     * Error Message to be displayed
     */
    private String message;
    
    public ErrorVO() {
    	
    }

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
}
