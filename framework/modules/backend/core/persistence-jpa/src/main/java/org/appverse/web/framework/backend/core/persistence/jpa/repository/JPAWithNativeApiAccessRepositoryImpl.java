package org.appverse.web.framework.backend.core.persistence.jpa.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link JPAWithNativeApiAccessRepository} implementation.
 * 
 * @author Miguel Fernandez
 */
@Repository
public class JPAWithNativeApiAccessRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements JPAWithNativeApiAccessRepository<T, ID> {

	private final EntityManager entityManager;


	/**
	 * Creates a new {@link JPAWithNativeApiAccessRepository}.
	 * @param domainClass
	 * @param entityManager
	 */
	public JPAWithNativeApiAccessRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
		this.entityManager = entityManager;
	}

	
	/* (non-Javadoc)
	 * @see org.appverse.web.framework.backend.core.persistence.jpa.repository.JPAWithNativeApiAccessRepository#unwrap(java.lang.Class)
	 */
	@Override
	public <C> C unwrap(Class<C> cls) {
		return entityManager.unwrap(cls);
	}
}