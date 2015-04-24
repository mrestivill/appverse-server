package org.appverse.web.framework.backend.frontfacade.rest.beans;

import org.appverse.web.framework.backend.core.beans.AbstractPresentationBean;

/**
 * Default basic ResponseData for REST calls.
 * Presentation objects extending this class will inherit the ErrorVO fields so that every REST call gets error information if any.
 * @see org.appverse.web.framework.backend.frontfacade.rest.handler.JerseyExceptionHandler
 * @author RRBL
 *
 */
public class ResponseDataVO extends AbstractPresentationBean {

	private ErrorVO errorVO = new ErrorVO();
	
	public ResponseDataVO() {
		
	}

	public ErrorVO getErrorVO() {
		return errorVO;
	}

	public void setErrorVO(ErrorVO errorVO) {
		this.errorVO = errorVO;
	}
}
