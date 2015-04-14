package org.appverse.web.framework.backend.frontfacade.rest.remotelog.managers;

import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogResponseVO;

/**
 * Interface for Remote Log manager 
 */
public interface RemoteLogManager  {

    public RemoteLogResponseVO writeLog(RemoteLogRequestVO remoteLogRequestVO) throws Exception;

}
