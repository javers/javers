package org.javers.spring.integration

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table;

/**
 * Created by gessnerfl on 21.02.15.
 */
@Entity
@Table(name = "dummy_object")
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
