package org.appverse.web.framework.backend.frontfacade.rest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class FrontFacadeModuleTestsConfigurationApplication extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(FrontFacadeModuleTestsConfigurationApplication.class);
	}

	public static void main(String[] args) {
		new FrontFacadeModuleTestsConfigurationApplication().configure(
				new SpringApplicationBuilder(FrontFacadeModuleTestsConfigurationApplication.class)).run(args);
	}

}
