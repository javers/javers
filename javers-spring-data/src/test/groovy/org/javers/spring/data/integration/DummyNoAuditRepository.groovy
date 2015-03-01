package org.javers.spring.data.integration

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by gessnerfl on 22.02.15.
 */
@Repository
interface DummyNoAuditRepository extends CrudRepository<DummyObject,String> {
}
