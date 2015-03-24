package org.appverse.web.framework.backend.core.persistence.services.integration;

public interface OrmNativeAPIRepository {
	
    /**
     * Wrapper of EntityManager unwrap method that provides the JPA provider
     * underlying session
     * @see javax.persistence.EntityManager#unwrap(Class)
     */
    <T> T unwrap(Class<T> cls);

}
