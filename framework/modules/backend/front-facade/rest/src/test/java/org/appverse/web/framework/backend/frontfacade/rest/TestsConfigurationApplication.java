package org.appverse.web.framework.backend.frontfacade.rest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.appverse.web.framework.backend.frontfacade.rest.remotelog")

public class TestsConfigurationApplication extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TestsConfigurationApplication.class);
	}

	public static void main(String[] args) {
		new TestsConfigurationApplication().configure(
				new SpringApplicationBuilder(TestsConfigurationApplication.class)).run(args);
	}

}
