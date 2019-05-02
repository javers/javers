package org.javers.core.model.subtyped

import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName

@TypeName("Supertype")
abstract class DummyAbstractSupertype {
    @Id int id

    String sharedValue

    DummyEntity sharedReference

    DummyAbstractSupertype(int id, String sharedValue, DummyEntity sharedReference) {
        this.id = id
        this.sharedValue = sharedValue
        this.sharedReference = sharedReference
    }

    int getId() {
        return id
    }

    void setId(int id) {
        this.id = id
    }

    String getSharedValue() {
        return sharedValue
    }

    void setSharedValue(String name) {
        this.sharedValue = name
    }

    DummyEntity getSharedReference() {
        return sharedReference
    }

    void setSharedReference(DummyEntity sharedReference) {
        this.sharedReference = sharedReference
    }
}
