package org.appverse.web.framework.backend.core.persistence.services.integration;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.appverse.web.framework.backend.core.persistence.services.integration.impl.live.JPAWithNativeApiAccessRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class JPAWithNativeApiAccessRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {

	@SuppressWarnings("rawtypes")
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
		return new JPAWithNativeApiAccessRepositoryFactory(em);
	}

	private static class JPAWithNativeApiAccessRepositoryFactory<T, I extends Serializable>
		extends JpaRepositoryFactory {

		private final EntityManager em;

		public JPAWithNativeApiAccessRepositoryFactory(EntityManager em) {
			super(em);
			this.em = em;
		}

		@SuppressWarnings("unchecked")
		protected Object getTargetRepository(RepositoryMetadata metadata) {
			return new JPAWithNativeApiAccessRepositoryImpl<T, I>((Class<T>) metadata.getDomainType(), em);
		}

		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return JPAWithNativeApiAccessRepositoryImpl.class;
		}
	}
}