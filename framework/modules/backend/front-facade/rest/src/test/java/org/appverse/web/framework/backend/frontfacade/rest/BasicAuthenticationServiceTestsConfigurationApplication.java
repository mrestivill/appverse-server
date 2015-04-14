package org.appverse.web.framework.backend.frontfacade.rest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class BasicAuthenticationServiceTestsConfigurationApplication extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BasicAuthenticationServiceTestsConfigurationApplication.class);
	}

	public static void main(String[] args) {
		new BasicAuthenticationServiceTestsConfigurationApplication().configure(
				new SpringApplicationBuilder(BasicAuthenticationServiceTestsConfigurationApplication.class)).run(args);
	}

}
