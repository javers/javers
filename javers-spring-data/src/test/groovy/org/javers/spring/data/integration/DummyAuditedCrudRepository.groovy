package org.javers.spring.data.integration

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.javers.spring.common.DummyObject
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by gessnerfl on 21.02.15.
 */
@Repository
@JaversSpringDataAuditable
interface DummyAuditedCrudRepository extends CrudRepository<DummyObject, String> {

}