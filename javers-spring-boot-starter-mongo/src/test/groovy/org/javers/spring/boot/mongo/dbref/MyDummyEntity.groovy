package org.javers.spring.boot.mongo.dbref

import org.javers.core.metamodel.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef

class MyDummyEntity {

    @Id
    private String id

    private String name

    @DBRef(lazy = true)
    private MyDummyRefEntity refEntity

    String getId() {
        return id
    }

    MyDummyRefEntity getRefEntity() {
        return refEntity
    }

    String getName() {
        return name
    }
}