package org.appverse.web.framework.backend.core.persistence.services.integration.impl.live;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.appverse.web.framework.backend.core.persistence.services.integration.JPAWithNativeApiAccessRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JPAWithNativeApiAccessRepositoryImpl implements JPAWithNativeApiAccessRepository {
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public <T> T unwrap(Class<T> cls) {
		return em.unwrap(cls);
	}

}
