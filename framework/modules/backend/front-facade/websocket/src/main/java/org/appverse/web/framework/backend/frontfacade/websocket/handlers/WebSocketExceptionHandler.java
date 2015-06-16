package org.appverse.web.framework.backend.frontfacade.websocket.handlers;/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0”). If a copy of the APL was not distributed with this 
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

import org.appverse.web.framework.backend.core.AbstractException;
import org.appverse.web.framework.backend.frontfacade.rest.beans.ErrorVO;
import org.appverse.web.framework.backend.frontfacade.rest.beans.ResponseDataVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;

import javax.servlet.http.HttpServletRequest;

@ConditionalOnProperty(value="appverse.frontfacade.websocket.exceptionHandler.enabled", matchIfMissing=true)
public class WebSocketExceptionHandler {
    @MessageExceptionHandler()
    @SendToUser("/queue/errors")
    public ResponseEntity<ResponseDataVO> handleError(HttpServletRequest req, Exception exception) {
        ResponseDataVO data = new ResponseDataVO();
        ErrorVO error = new ErrorVO();

        Long code = 500L;
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if(exception instanceof AbstractException) {
            if (((AbstractException) exception).getCode() != null) {
                code = ((AbstractException) exception).getCode();
            }
        }
        error.setCode(code);
        error.setMessage(exception.getMessage());
        data.setErrorVO(error);
        return  new ResponseEntity<ResponseDataVO>(data, httpStatus);
    }
}
