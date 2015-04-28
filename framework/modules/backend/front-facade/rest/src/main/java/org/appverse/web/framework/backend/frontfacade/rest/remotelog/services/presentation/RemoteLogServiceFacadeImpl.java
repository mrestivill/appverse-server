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
package org.appverse.web.framework.backend.frontfacade.rest.remotelog.services.presentation;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.appverse.web.framework.backend.core.services.AbstractPresentationService;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.RemoteLogConstants;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.managers.RemoteLogManager;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogRequestVO;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.model.presentation.RemoteLogResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Singleton
@Path("/remotelog")
/**
 * {@link RemoteLogServiceFacade} JAX-RS (Jersey) implementation that exposes a Remote Log REST service.
 */
public class RemoteLogServiceFacadeImpl extends AbstractPresentationService implements  RemoteLogServiceFacade{


    @Autowired
    RemoteLogManager remoteLogManager;

    /**
     * Writes a remote log
     * @param remoteLogRequestVO
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("log")
    public Response writeRemoteLog(RemoteLogRequestVO remoteLogRequestVO) {
        RemoteLogResponseVO remoteLogResponseVO = null;
        try{
            remoteLogResponseVO = remoteLogManager.writeLog(remoteLogRequestVO);
            if (!remoteLogResponseVO.getStatus().equals(RemoteLogConstants.OK)){
                // Error related to client call
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        catch(Exception e){
            // Server error
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

}