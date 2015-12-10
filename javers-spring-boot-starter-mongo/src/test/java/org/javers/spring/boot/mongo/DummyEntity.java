package org.javers.spring.boot.mongo;

import org.javers.core.metamodel.annotation.Id;

public class DummyEntity {

    private final int id;

    public DummyEntity(int id) {
        this.id = id;
    }

    @Id
    public int getId() {
        return id;
    }
}
