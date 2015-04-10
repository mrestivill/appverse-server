package org.appverse.web.framework.backend.frontfacade.rest.remotelog.managers;

import org.appverse.web.framework.backend.core.enterprise.log.AutowiredLogger;
import org.appverse.web.framework.backend.core.services.AbstractPresentationService;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogResponseVO;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RemoteLogManagerImpl extends AbstractPresentationService implements RemoteLogManager {

    @AutowiredLogger
    private static Logger logger;

    @Override
    public RemoteLogResponseVO writeLog(RemoteLogRequestVO remoteLogRequestVO) throws Exception {
        RemoteLogResponseVO remoteLogResponseVO = new RemoteLogResponseVO();
        remoteLogResponseVO.setStatus(RemoteLogResponseVO.OK);
        String s = remoteLogRequestVO.getLogLevel().toUpperCase();
        if(s.equals("DEBUG")){
            logger.debug(remoteLogRequestVO.getMessage());
        }
        else if(s.equals("INFO")){
            logger.info(remoteLogRequestVO.getMessage());
        }
        else if(s.equals("WARN")){
            logger.warn(remoteLogRequestVO.getMessage());
        }
        else if(s.equals("ERROR")){
            logger.error(remoteLogRequestVO.getMessage());
        }
        else if(s.equals("TRACE")){
            logger.trace(remoteLogRequestVO.getMessage());
        }
        else{
            // If the trace level is not recognized we write the log anyway with "DEBUG" level so we don't lose
            // information in the log but we return an error
            logger.debug(remoteLogRequestVO.getMessage());
            remoteLogResponseVO.setStatus(RemoteLogResponseVO.ERROR);
            remoteLogResponseVO.setMessage("Unrecognized log level. The log was written with default DEBUG level. Valid values are DEBUG, INFO, WARN, ERROR, TRACE");
        }
        return remoteLogResponseVO;
    }
}