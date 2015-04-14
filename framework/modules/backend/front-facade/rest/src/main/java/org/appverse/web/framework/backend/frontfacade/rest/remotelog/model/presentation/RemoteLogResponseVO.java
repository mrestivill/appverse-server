package org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation;

import org.appverse.web.framework.backend.core.beans.AbstractPresentationBean;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.managers.RemoteLogManager;

/**
 * Models a remote log response to use with a {@link RemoteLogManager}
 */
public class RemoteLogResponseVO extends AbstractPresentationBean{

	private static final long serialVersionUID = 7731734462869651852L;

    String status;

    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
