package org.javers.spring.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by gessnerfl on 22.02.15.
 */
@Repository
interface DummyNoAuditCrudRepository extends JpaRepository<DummyObject,String> {
}
