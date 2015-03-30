/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.appverse.web.framework.backend.core.persistence.test.jpa.eclipselink.services.integration;

import org.appverse.web.framework.backend.core.persistence.jpa.repository.JPAWithNativeApiAccessRepository;
import org.appverse.web.framework.backend.core.persistence.test.jpa.eclipselink.model.integration.TestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Simple repository to support this module tests 
 * 
 * @author Miguel Fernandez
 *
 */
public interface TestRepository extends JPAWithNativeApiAccessRepository<TestDTO, Long> {

	Page<TestDTO> findAll(Pageable pageable);

	Page<TestDTO> findByField1ContainingAndField2ContainingAllIgnoringCase(String field1,
			String field2, Pageable pageable);

	TestDTO findByField1AndField2AllIgnoringCase(String field1, String field2);

}
