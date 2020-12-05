package org.javers.spring.example;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
class DummyObject {
    @Id
    String id;
    String name;

    public String getName() {
        return name;
    }
}
