package org.javers.spring.auditable.integration

import javax.persistence.Id;

/**
 * Created by gessnerfl on 21.02.15.
 */
class DummyObject {
    @Id
    String id
    String name

    DummyObject(String name) {
        this.name = name
        this.id = UUID.randomUUID().toString()
    }
}
