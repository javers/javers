package org.javers.spring.boot.mongo.dbref

import org.springframework.data.repository.CrudRepository

interface MyDummyRefEntityRepository extends CrudRepository<MyDummyRefEntity, String> {
}