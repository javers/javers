package org.javers.spring.repository

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.javers.spring.model.DummyObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@JaversSpringDataAuditable
interface DummyAuditedJpaRepository extends JpaRepository<DummyObject, String> {
}