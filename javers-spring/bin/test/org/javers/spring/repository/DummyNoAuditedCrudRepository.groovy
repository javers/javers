package org.javers.spring.repository

import org.javers.spring.model.DummyObject
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DummyNoAuditedCrudRepository extends CrudRepository<DummyObject, String> {
}