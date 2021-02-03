package org.javers.spring.boot.mongo.dbref

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.repository.CrudRepository

@JaversSpringDataAuditable
interface MyDummyEntityRepository extends CrudRepository<MyDummyEntity, String> {
}