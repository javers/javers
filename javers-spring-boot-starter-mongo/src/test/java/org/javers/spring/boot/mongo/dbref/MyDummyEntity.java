package org.javers.spring.boot.mongo.dbref;

import org.javers.core.metamodel.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class MyDummyEntity {
    private String id;

    @DBRef(lazy = true)
    private MyDummyRefEntity refEntity;

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MyDummyRefEntity getRefEntity() {
        return refEntity;
    }

    public void setRefEntity(MyDummyRefEntity refEntity) {
        this.refEntity = refEntity;
    }
}