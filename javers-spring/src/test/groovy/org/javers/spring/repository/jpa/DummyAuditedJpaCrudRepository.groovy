package org.javers.spring.repository.jpa

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.javers.spring.model.DummyObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by gessnerfl on 21.02.15.
 */
@Repository
@JaversSpringDataAuditable
interface DummyAuditedJpaCrudRepository extends JpaRepository<DummyObject, String> {

}