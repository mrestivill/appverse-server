package org.appverse.web.framework.backend.core.enterprise.aop.autoconfigure;

import org.appverse.web.framework.backend.core.enterprise.aop.managers.ExceptionManager;
import org.appverse.web.framework.backend.core.enterprise.aop.managers.impl.live.ExceptionManagerImpl;
import org.springframework.context.annotation.Bean;


public class AOPExceptionManagerAutoConfiguration {
	@Bean
	public static ExceptionManager exceptionManager() {
		return new ExceptionManagerImpl();
	}
}
