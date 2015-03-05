package org.javers.spring.integration

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.javers.spring.integration.DummyObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Created by gessnerfl on 21.02.15.
 */
@Repository
@JaversSpringDataAuditable
interface DummyAuditedCrudRepository extends JpaRepository<DummyObject, String> {

}