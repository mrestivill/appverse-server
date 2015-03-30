package org.appverse.web.framework.backend.core.persistence.test.jpa.eclipselink;

import java.util.HashMap;
import java.util.Map;

import org.appverse.web.framework.backend.core.persistence.jpa.repository.support.JPAWithNativeApiAccessRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

/**
 * Configuration class to support this module tests
 * 
 * @author Miguel Fernandez
 */
@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(repositoryFactoryBeanClass = JPAWithNativeApiAccessRepositoryFactoryBean.class)
public class TestConfiguration extends JpaBaseConfiguration{

	@Override
	protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
		EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
		return adapter;
	}

	@Override
	protected Map<String, Object> getVendorProperties() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		return map;
	}

}