package org.javers.spring.model

import org.javers.spring.jpa.JaversEntityListener

import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Id

@EntityListeners([JaversEntityListener])
@Entity
class DummyObjectAuditable {

    @Id
    String id
    String name

    DummyObjectAuditable() {
        this.id = UUID.randomUUID().toString()
    }

    DummyObjectAuditable(String name) {
        this.name = name
        this.id = UUID.randomUUID().toString()
    }
}
