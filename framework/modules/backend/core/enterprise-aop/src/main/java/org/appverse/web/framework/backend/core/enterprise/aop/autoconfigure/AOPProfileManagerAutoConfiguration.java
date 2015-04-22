package org.appverse.web.framework.backend.core.enterprise.aop.autoconfigure;

import org.appverse.web.framework.backend.core.enterprise.aop.advices.ProfilingAdvice;
import org.appverse.web.framework.backend.core.enterprise.aop.managers.ProfileManager;
import org.appverse.web.framework.backend.core.enterprise.aop.managers.impl.live.ProfileManagerImpl;
import org.springframework.context.annotation.Bean;


public class AOPProfileManagerAutoConfiguration {
	@Bean
	public static ProfileManager profileManager() {
		return new ProfileManagerImpl();
	}
	
	@Bean
	public static ProfilingAdvice profilingAdvice() {
		return new ProfilingAdvice();
	}
}
