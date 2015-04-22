/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0�?). If a copy of the APL was not distributed with this 
 file, You can obtain one at http://www.appverse.mobi/licenses/apl_v2.0.pdf. [^]

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the conditions of the AppVerse Public License v2.0 
 are met.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. EXCEPT IN CASE OF WILLFUL MISCONDUCT OR GROSS NEGLIGENCE, IN NO EVENT
 SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) 
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.
 */
package org.appverse.web.framework.backend.frontfacade.rest.remotelog.managers;

import org.appverse.web.framework.backend.core.enterprise.log.AutowiredLogger;
import org.appverse.web.framework.backend.core.services.AbstractPresentationService;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.RemoteLogConstants;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogResponseVO;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * {@link RemoteLogManager} implementation that mimics and delegates in SLF4J log levels to write
 * a remote log trace. RemoteLogResponseVO valid log levels are SLF4J log levels, otherwise an error message is logged.
 */
@Component
public class RemoteLogManagerImpl extends AbstractPresentationService implements RemoteLogManager {

    @AutowiredLogger
    private static Logger logger;

    @Override
    public RemoteLogResponseVO writeLog(RemoteLogRequestVO remoteLogRequestVO) throws Exception {
        RemoteLogResponseVO remoteLogResponseVO = new RemoteLogResponseVO();
        remoteLogResponseVO.setStatus(RemoteLogConstants.OK);
        String s = remoteLogRequestVO.getLogLevel().toUpperCase();
        if(s.equals(RemoteLogConstants.LEVEL_DEBUG)){
            logger.debug(remoteLogRequestVO.getMessage());
        }
        else if(s.equals(RemoteLogConstants.LEVEL_INFO)){
            logger.info(remoteLogRequestVO.getMessage());
        }
        else if(s.equals(RemoteLogConstants.LEVEL_WARN)){
            logger.warn(remoteLogRequestVO.getMessage());
        }
        else if(s.equals(RemoteLogConstants.LEVEL_ERROR)){
            logger.error(remoteLogRequestVO.getMessage());
        }
        else if(s.equals(RemoteLogConstants.LEVEL_TRACE)){
            logger.trace(remoteLogRequestVO.getMessage());
        }
        else{
            // If the trace level is not recognized we write the log anyway with "DEBUG" level so we don't lose
            // information in the log but we return an error
            logger.debug(remoteLogRequestVO.getMessage());
            remoteLogResponseVO.setStatus(RemoteLogConstants.ERROR);
            remoteLogResponseVO.setMessage("Unrecognized log level. The log was written with default DEBUG level. Valid values are DEBUG, INFO, WARN, ERROR, TRACE");
        }
        return remoteLogResponseVO;
    }
}