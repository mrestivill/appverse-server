package org.appverse.web.framework.backend.frontfacade.rest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.appverse.web.framework.backend.frontfacade.rest.remotelog")

/* Important: @ImportResource allows you to load a context from XML (traditional way). However, we should avoid this...
@ImportResource({
    "classpath:application-config.xml"
})
*/
public class TestsConfigurationApplication extends SpringBootServletInitializer {
	
	// http://www.leveluplunch.com/blog/2014/04/01/spring-boot-configure-servlet-mapping-filters/
	// http://stackoverflow.com/questions/26994663/how-to-configure-display-name-when-no-web-xml
	// <display-name>${actifactId}</display-name>
	// org-appverse-bank-sts (uat)	
	
	// Profiles
	// https://github.com/spring-projects/spring-boot/issues/1095

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TestsConfigurationApplication.class);
	}

	public static void main(String[] args) {
		new TestsConfigurationApplication().configure(
				new SpringApplicationBuilder(TestsConfigurationApplication.class)).run(args);
	}

}
