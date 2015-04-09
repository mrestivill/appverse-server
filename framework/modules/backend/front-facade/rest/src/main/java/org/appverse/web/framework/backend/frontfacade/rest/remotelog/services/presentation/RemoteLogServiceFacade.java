package org.appverse.web.framework.backend.frontfacade.rest.remotelog.services.presentation;

import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogResponseVO;

public interface RemoteLogServiceFacade  {

    public RemoteLogResponseVO writeLog(RemoteLogRequestVO remoteLogRequestVO) throws Exception;

}
