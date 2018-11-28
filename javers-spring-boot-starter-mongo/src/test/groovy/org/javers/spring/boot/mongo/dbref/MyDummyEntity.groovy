package org.javers.spring.boot.mongo.dbref

import org.javers.core.metamodel.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef

class MyDummyEntity {
    private String id

    @DBRef(lazy = true)
    private MyDummyRefEntity refEntity

    @Id
    String getId() {
        return id
    }

    MyDummyRefEntity getRefEntity() {
        return refEntity
    }
}