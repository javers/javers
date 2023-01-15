package org.javers.spring.model

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class DummyObject {
    @Id
    String id
    String name

    DummyObject() {
        this.id = UUID.randomUUID().toString()
    }

    DummyObject(String name) {
        this.name = name
        this.id = UUID.randomUUID().toString()
    }
}
