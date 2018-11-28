package org.javers.spring.boot.mongo.dbref;

import org.javers.core.metamodel.annotation.Id;

public class MyDummyRefEntity {

    private  String id;

    private String name;

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}