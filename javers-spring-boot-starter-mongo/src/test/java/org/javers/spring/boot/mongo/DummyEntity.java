package org.javers.spring.boot.mongo;

import org.javers.core.metamodel.annotation.Id;

/**
 * @author pawelszymczyk
 */
public class DummyEntity {

    @Id
    private final int id;

    public DummyEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
