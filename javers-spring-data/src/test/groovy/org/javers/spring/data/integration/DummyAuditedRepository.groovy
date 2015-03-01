package org.javers.spring.data.integration

import org.javers.spring.data.JaversSpringDataAuditable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by gessnerfl on 21.02.15.
 */
@Repository
@JaversSpringDataAuditable
interface DummyAuditedRepository extends CrudRepository<DummyObject, String> {

}