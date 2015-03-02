package org.javers.spring.auditable.integration

import org.javers.spring.model.DummyObject
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by gessnerfl on 22.02.15.
 */
@Repository
interface DummyNoAuditCrudRepository extends CrudRepository<DummyObject,String> {
}
