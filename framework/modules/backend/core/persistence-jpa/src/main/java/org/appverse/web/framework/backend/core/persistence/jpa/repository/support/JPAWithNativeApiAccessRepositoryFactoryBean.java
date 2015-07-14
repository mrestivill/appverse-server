package org.appverse.web.framework.backend.core.persistence.jpa.repository.support;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.appverse.web.framework.backend.core.persistence.jpa.repository.JPAWithNativeApiAccessRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * Specialization of  {@link org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean} for 
 * {@link org.appverse.web.framework.backend.core.persistence.jpa.repository.JPAWithNativeApiAccessRepositoryImpl} 
 * @param <T> the type of the repository
 */
public class JPAWithNativeApiAccessRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {

	@SuppressWarnings("rawtypes")
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
		return new JPAWithNativeApiAccessRepositoryFactory(em);
	}

	/**
	 * Specific factory for {@link JpaRepositoryFactory}
	 * @param <T>
	 * @param <I>
	 */
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