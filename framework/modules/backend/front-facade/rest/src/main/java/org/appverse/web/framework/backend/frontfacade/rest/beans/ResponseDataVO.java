package org.appverse.web.framework.backend.frontfacade.rest.beans;

import org.appverse.web.framework.backend.core.beans.AbstractPresentationBean;

public class ResponseDataVO extends AbstractPresentationBean {

	private ErrorVO errorVO;
	
	public ResponseDataVO() {
		
	}

	public ErrorVO getErrorVO() {
		return errorVO;
	}

	public void setErrorVO(ErrorVO errorVO) {
		this.errorVO = errorVO;
	}
}
