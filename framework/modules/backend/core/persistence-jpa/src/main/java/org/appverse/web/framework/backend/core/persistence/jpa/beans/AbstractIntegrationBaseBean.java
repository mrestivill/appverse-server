package org.appverse.web.framework.backend.core.persistence.jpa.beans;

import javax.persistence.MappedSuperclass;
import org.appverse.web.framework.backend.core.beans.AbstractIntegrationBean;

@MappedSuperclass
public abstract class AbstractIntegrationBaseBean extends AbstractIntegrationBean{

        private static final long serialVersionUID = -2070164067618480119L;
        protected Long id;

        public void setId(Long id) {
            this.id = id;
        }

        // Always add condition "if (id == 0 || id != other.id)" so in case that
        // Dozer non-cummulative collections and remove-orphans in mappings works fine
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            AbstractIntegrationBaseBean other = (AbstractIntegrationBaseBean) obj;
            if (id == 0 || id != other.id) {
                return false;
            }
            return true;
        }

        // Required so that Dozer non-cummulative collections and remove-orphans in
        // mappings works fine
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (id ^ id >>> 32);
            return result;
        }
}
