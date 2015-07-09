package org.appverse.web.framework.backend.test.util.frontfacade.mvc.tests.predefined;

import org.appverse.web.framework.backend.security.authentication.userpassword.model.AuthorizationData;

public class TestLoginInfo {
	
	String jsessionid;
	
	AuthorizationData authorizationData;
	
	String xsrfToken;

	
	public AuthorizationData getAuthorizationData() {
		return authorizationData;
	}

	public void setAuthorizationData(AuthorizationData authorizationData) {
		this.authorizationData = authorizationData;
	}

	public String getJsessionid() {
		return jsessionid;
	}

	public void setJsessionid(String jsessionid) {
		this.jsessionid = jsessionid;
	}

	public String getXsrfToken() {
		return xsrfToken;
	}

	public void setXsrfToken(String xsrfToken) {
		this.xsrfToken = xsrfToken;
	}

}
