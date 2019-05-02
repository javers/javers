package org.javers.core.model.subtyped.value

import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.model.subtyped.DummyAbstractSupertype

@TypeName("Supertype")
class ConcreteSubtype2WithValueField extends DummyAbstractSupertype{
    String concreteTypeTwoProperty

    ConcreteSubtype2WithValueField(int id, String sharedValue, String concreteTypeTwoProperty) {
        super(id, sharedValue, null)
        this.concreteTypeTwoProperty = concreteTypeTwoProperty
    }

    String getConcreteTypeTwoProperty() {
        return concreteTypeTwoProperty
    }

    void setConcreteTypeTwoProperty(String concreteTypeTwoProperty) {
        this.concreteTypeTwoProperty = concreteTypeTwoProperty
    }
}
