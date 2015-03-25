package org.appverse.web.framework.backend.core.persistence.test;

import org.appverse.web.framework.backend.core.persistence.services.integration.JPAWithNativeApiAccessRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(repositoryFactoryBeanClass = JPAWithNativeApiAccessRepositoryFactoryBean.class)
public class TestConfiguration {

}