package org.javers.spring.repository

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.javers.spring.model.DummyObject
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@JaversSpringDataAuditable
interface DummyAuditedCrudRepository extends CrudRepository<DummyObject, String> {
}