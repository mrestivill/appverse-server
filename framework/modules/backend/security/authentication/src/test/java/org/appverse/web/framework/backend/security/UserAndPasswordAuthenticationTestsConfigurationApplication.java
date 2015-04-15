package org.appverse.web.framework.backend.security;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class UserAndPasswordAuthenticationTestsConfigurationApplication extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(UserAndPasswordAuthenticationTestsConfigurationApplication.class);
	}

	public static void main(String[] args) {
		new UserAndPasswordAuthenticationTestsConfigurationApplication().configure(
				new SpringApplicationBuilder(UserAndPasswordAuthenticationTestsConfigurationApplication.class)).run(args);
	}

}
