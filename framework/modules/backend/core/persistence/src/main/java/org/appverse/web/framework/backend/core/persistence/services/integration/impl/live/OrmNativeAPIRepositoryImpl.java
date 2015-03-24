package org.appverse.web.framework.backend.core.persistence.services.integration.impl.live;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.appverse.web.framework.backend.core.persistence.services.integration.OrmNativeAPIRepository;

public class OrmNativeAPIRepositoryImpl implements OrmNativeAPIRepository {
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public <T> T unwrap(Class<T> cls) {
		return em.unwrap(cls);
	}

}
