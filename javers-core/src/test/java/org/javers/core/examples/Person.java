package org.javers.core.examples;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

@TypeName("Person")
class Person {
    @Id
    private int id;

    private String name;

    private Address address;

    public Person() {
    }

    Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    Person(int id, Address address) {
        this.id = id;
        this.address = address;
    }
}
