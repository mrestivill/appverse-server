package org.appverse.web.framework.backend.core.enterprise.aop.autoconfigure;

import org.appverse.web.framework.backend.core.enterprise.aop.advices.ExceptionAdvice;
import org.appverse.web.framework.backend.core.enterprise.aop.managers.ExceptionManager;
import org.appverse.web.framework.backend.core.enterprise.aop.managers.impl.live.ExceptionManagerImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;


/**
 * Autoconfiguration class for the Exception Manager component.
 * In case your app wants to use the Exception Manager:
 * 	1- Add the core-enterprise-module to your pom
 *  2- Define your aop config in the application.xml file as follows:
 *		<aop:advisor advice-ref="exceptionAdvice" pointcut="org.appverse.web.framework.backend.core.enterprise.aop.pointcut.AppverseAOPPointcuts.allServicesCalls()" />
 * In case you don't want to use it, just do not set the module as dependency, and if that's not possible, exlude this autoconfiguration
 * 	@EnableAutoConfiguration(exclude={AOPProfileManagerAutoConfiguration.class})
 *  
 * @author RRBL
 *
 */
public class AOPExceptionManagerAutoConfiguration {
	@Bean
	public static ExceptionManager exceptionManager() {
		return new ExceptionManagerImpl();
	}
	@Bean
	public static ExceptionAdvice exceptionAdvice() {
		return new ExceptionAdvice();
	}
}
