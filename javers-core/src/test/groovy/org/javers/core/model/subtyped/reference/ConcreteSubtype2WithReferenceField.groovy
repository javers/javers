package org.javers.core.model.subtyped.reference

import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.model.subtyped.DummyAbstractSupertype
import org.javers.core.model.subtyped.DummyEntity

@TypeName("Supertype")
class ConcreteSubtype2WithReferenceField extends DummyAbstractSupertype {

    DummyEntity typeTwoDummyEntity

    ConcreteSubtype2WithReferenceField(int id, String name, DummyEntity sharedReference, DummyEntity typeTwoDummyEntity) {
        super(id, name, sharedReference)
        this.typeTwoDummyEntity = typeTwoDummyEntity
    }

    DummyEntity getTypeTwoDummyEntity() {
        return typeTwoDummyEntity
    }

    void setTypeTwoDummyEntity(DummyEntity typeTwoDummyEntity) {
        this.typeTwoDummyEntity = typeTwoDummyEntity
    }
}
