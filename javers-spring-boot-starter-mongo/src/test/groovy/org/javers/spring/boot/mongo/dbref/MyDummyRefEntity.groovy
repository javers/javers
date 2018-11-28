package org.javers.spring.boot.mongo.dbref

import org.javers.core.metamodel.annotation.Id

class MyDummyRefEntity {

    private  String id

    private String name

    @Id
    String getId() {
        return id
    }

    String getName() {
        return name
    }
}