package org.appverse.web.framework.backend.core.persistence.test.jpa.hibernate;

import org.appverse.web.framework.backend.core.persistence.jpa.repository.support.JPAWithNativeApiAccessRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class to support this module tests
 * 
 * @author Miguel Fernandez
 */
@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(repositoryFactoryBeanClass = JPAWithNativeApiAccessRepositoryFactoryBean.class)
public class TestConfiguration {

}