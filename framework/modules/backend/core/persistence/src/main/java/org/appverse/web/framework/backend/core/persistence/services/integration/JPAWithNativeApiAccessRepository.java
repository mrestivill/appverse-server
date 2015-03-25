package org.appverse.web.framework.backend.core.persistence.services.integration;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JPAWithNativeApiAccessRepository <T, ID extends Serializable> extends JpaRepository<T, ID>{
	
    /**
     * Wrapper of EntityManager unwrap method that provides the JPA provider
     * underlying session
     * @see javax.persistence.EntityManager#unwrap(Class)
     */
    <C> C unwrap(Class<C> cls);

}
