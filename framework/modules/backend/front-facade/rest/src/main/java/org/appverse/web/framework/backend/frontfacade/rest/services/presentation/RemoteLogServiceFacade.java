package org.appverse.web.framework.backend.frontfacade.rest.services.presentation;

import org.appverse.web.framework.backend.frontfacade.rest.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.frontfacade.rest.model.presentation.RemoteLogResponseVO;

public interface RemoteLogServiceFacade  {

    public RemoteLogResponseVO writeLog(RemoteLogRequestVO remoteLogRequestVO) throws Exception;

}
