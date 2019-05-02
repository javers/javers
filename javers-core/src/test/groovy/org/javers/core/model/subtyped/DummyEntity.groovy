package org.javers.core.model.subtyped

import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName

@TypeName("DummyEntity")
class DummyEntity {
    @Id Integer id
    String name

    DummyEntity(Integer id, String name) {
        this.id = id
        this.name = name
    }

    Integer getId() {
        return id
    }

    void setId(Integer id) {
        this.id = id
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }
}
