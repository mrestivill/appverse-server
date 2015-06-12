package org.appverse.web.framework.backend.core.enterprise.converters.autoconfigure;

import java.io.IOException;

import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@ConditionalOnClass(DozerBeanMapperFactoryBean.class)
public class ConvertersAutoConfiguration {

	@Autowired
	private ApplicationContext appContext;

	@Bean(name = "appverseConvertersService")
	public DozerBeanMapperFactoryBean dozerFactoryBean() {
		DozerBeanMapperFactoryBean dozerFactoryBean = new DozerBeanMapperFactoryBean();	    
		Resource[] dozerConfigFiles=null;
		try {
			dozerConfigFiles = appContext.getResources("classpath*:dozer/*bean-mappings.xml");			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dozerFactoryBean.setMappingFiles(dozerConfigFiles);
		return dozerFactoryBean;
	}	

}