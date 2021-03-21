package org.javers.spring.boot.mongo.dbref

import org.javers.core.metamodel.annotation.Id

class MyDummyRefEntity {

    @Id
    private  String id
    private String name


    String getId() {
        return id
    }

    String getName() {
        return name
    }
}