package org.javers.spring.auditable.integration

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by gessnerfl on 21.02.15.
 */
@Repository
@JaversSpringDataAuditable
interface DummyAuditedCrudRepository extends CrudRepository<DummyObject, String> {

}