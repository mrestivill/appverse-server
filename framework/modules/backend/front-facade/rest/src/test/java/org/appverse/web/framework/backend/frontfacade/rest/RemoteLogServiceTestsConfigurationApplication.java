package org.appverse.web.framework.backend.frontfacade.rest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class RemoteLogServiceTestsConfigurationApplication extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RemoteLogServiceTestsConfigurationApplication.class);
	}

	public static void main(String[] args) {
		new RemoteLogServiceTestsConfigurationApplication().configure(
				new SpringApplicationBuilder(RemoteLogServiceTestsConfigurationApplication.class)).run(args);
	}

}
