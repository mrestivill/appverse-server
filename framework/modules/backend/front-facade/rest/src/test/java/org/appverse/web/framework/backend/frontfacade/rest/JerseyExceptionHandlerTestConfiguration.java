package org.appverse.web.framework.backend.frontfacade.rest;
import javax.annotation.PostConstruct;

import org.appverse.web.framework.backend.frontfacade.rest.autoconfigure.FrontFacadeRestAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
 
@Configuration
@ConditionalOnClass(name = {"org.glassfish.jersey.server.spring.SpringComponentProvider",
        "javax.servlet.ServletRegistration",
        "org.appverse.web.framework.backend.frontfacade.rest.autoconfigure.FrontFacadeRestAutoConfiguration"})
@ConditionalOnBean(type = "org.glassfish.jersey.server.ResourceConfig")
public class JerseyExceptionHandlerTestConfiguration {
     
    @Autowired
    private FrontFacadeRestAutoConfiguration frontFacadeRestAutoConfiguration; 
     
    public JerseyExceptionHandlerTestConfiguration() {
    }
     
    /*
     * Init method requires to be annotated with {@link PostConstruct} as we need properties to be injected
     * Registering here the custom jersey resource used in tests
     */
    @PostConstruct
    public void init() {
        frontFacadeRestAutoConfiguration.register(TestExceptionHandlingResource.class);
    }
}