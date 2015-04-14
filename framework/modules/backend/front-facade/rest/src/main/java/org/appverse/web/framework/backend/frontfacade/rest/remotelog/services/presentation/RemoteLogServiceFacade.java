package org.appverse.web.framework.backend.frontfacade.rest.remotelog.services.presentation;

import javax.ws.rs.core.Response;

import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;

public interface RemoteLogServiceFacade {
	
	Response writeRemoteLog(RemoteLogRequestVO remoteLogRequestVO);
	
}
