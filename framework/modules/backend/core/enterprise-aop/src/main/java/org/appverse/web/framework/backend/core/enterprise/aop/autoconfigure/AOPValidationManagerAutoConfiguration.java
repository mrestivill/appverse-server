package org.appverse.web.framework.backend.core.enterprise.aop.autoconfigure;

import org.appverse.web.framework.backend.core.enterprise.aop.managers.ValidationManager;
import org.appverse.web.framework.backend.core.enterprise.aop.managers.impl.live.ValidationManagerImpl;
import org.springframework.context.annotation.Bean;


public class AOPValidationManagerAutoConfiguration {
	@Bean
	public static ValidationManager validationManager() {
		return new ValidationManagerImpl();
	}
}
