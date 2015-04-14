package org.appverse.web.framework.backend.frontfacade.rest.remotelog.services.presentation;

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