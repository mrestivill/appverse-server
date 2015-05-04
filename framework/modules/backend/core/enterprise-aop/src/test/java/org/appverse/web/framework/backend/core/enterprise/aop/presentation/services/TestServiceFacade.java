package org.appverse.web.framework.backend.core.enterprise.aop.presentation.services;

import org.appverse.web.framework.backend.core.services.AbstractPresentationService;

public class TestServiceFacade extends AbstractPresentationService {
	
	public void aMethodWithErrors() {
		//this will throw a NullPointerException
		String s = null;
		int i = s.length();
	}

}
