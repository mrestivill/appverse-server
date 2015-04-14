package org.appverse.web.framework.backend.frontfacade.rest.remotelog.services.presentation;

import javax.ws.rs.core.Response;

import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;

/**
 * Remote log REST service 
 */
public interface RemoteLogServiceFacade {
	
	Response writeRemoteLog(RemoteLogRequestVO remoteLogRequestVO);
	
}
