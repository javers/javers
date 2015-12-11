package org.javers.core.examples;

import org.javers.core.metamodel.annotation.Id;

class PersonSimple {
    @Id
    private int id;

    private String name;

    PersonSimple(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
