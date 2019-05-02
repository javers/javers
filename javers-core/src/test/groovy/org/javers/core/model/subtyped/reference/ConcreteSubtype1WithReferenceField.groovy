package org.javers.core.model.subtyped.reference

import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.model.subtyped.DummyAbstractSupertype
import org.javers.core.model.subtyped.DummyEntity

@TypeName("Supertype")
class ConcreteSubtype1WithReferenceField extends DummyAbstractSupertype {

    DummyEntity typeOneDummyEntity

    ConcreteSubtype1WithReferenceField(int id, String sharedValue, DummyEntity sharedReference, DummyEntity typeOneDummyEntity) {
        super(id, sharedValue, sharedReference)
        this.typeOneDummyEntity = typeOneDummyEntity
    }

    DummyEntity getTypeOneDummyEntity() {
        return typeOneDummyEntity
    }

    void setTypeOneDummyEntity(DummyEntity typeOneDummyEntity) {
        this.typeOneDummyEntity = typeOneDummyEntity
    }
}
