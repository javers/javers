package org.javers.spring.repository.jpa

import org.javers.spring.model.DummyObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by gessnerfl on 22.02.15.
 */
@Repository
interface DummyNoAuditJpaCrudRepository extends JpaRepository<DummyObject,String> {
}
