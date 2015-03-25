package org.appverse.web.framework.backend.core.persistence.services.integration.impl.live;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.appverse.web.framework.backend.core.persistence.services.integration.JPAWithNativeApiAccessRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JPAWithNativeApiAccessRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements JPAWithNativeApiAccessRepository<T, ID> {

	private final EntityManager entityManager;

	public JPAWithNativeApiAccessRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	public <C> C unwrap(Class<C> cls) {
		return entityManager.unwrap(cls);
	}
}