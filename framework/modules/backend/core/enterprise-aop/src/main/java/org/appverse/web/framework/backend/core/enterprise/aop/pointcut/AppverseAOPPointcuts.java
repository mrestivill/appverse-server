package org.appverse.web.framework.backend.core.enterprise.aop.pointcut;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Appverse predefined pointcuts that can be used in aop configuration file.
 * 
 * @author RRBL
 *
 */
public class AppverseAOPPointcuts {

	/**
	 * Pointcut for all classes extending AbstractPresentationService
	 */
	@Pointcut("execution(public * org.appverse.web.framework.backend.core.services.AbstractPresentationService+.*(..))")
	public void allPresentationServices() {
	}

	/**
	 * Pointcut for all classes extending AbstractBusinessService
	 */
	@Pointcut("execution(public * org.appverse.web.framework.backend.core.services.AbstractBusinessService+.*(..))")
	public void allBusinessServices() {
	}

	/**
	 * Pointcut for all classes extending spring data Repository
	 */
	@Pointcut("execution(public * org.springframework.data.repository.Repository+.*(..))")
	public void allIntegrationDataServices() {
	}

	/**
	 * Pointcut grouping presentation and business services
	 */
	@Pointcut("allPresentationServices() || allBusinessServices()")
	public void allPresentationOrBusinessServices() {
	}

	/**
	 * Pointcut for all services in all layers
	 */
	@Pointcut("allPresentationServices() || allBusinessServices() || allIntegrationDataServices()")
	public void allServicesCalls() {
	}

}
