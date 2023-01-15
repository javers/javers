package org.javers.spring.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
class DummyObject {
    @Id
    String id;
    String name;

    public String getName() {
        return name;
    }
}
