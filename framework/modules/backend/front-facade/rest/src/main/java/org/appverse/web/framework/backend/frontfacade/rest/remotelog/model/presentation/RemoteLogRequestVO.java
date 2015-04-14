package org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation;

import org.appverse.web.framework.backend.core.beans.AbstractPresentationBean;

public class RemoteLogRequestVO extends AbstractPresentationBean{

	private static final long serialVersionUID = -4324332467995573556L;

	String logLevel;

    String message;

    String userAgent;

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
