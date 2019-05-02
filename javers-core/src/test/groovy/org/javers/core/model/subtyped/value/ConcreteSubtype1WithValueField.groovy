package org.javers.core.model.subtyped.value

import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.model.subtyped.DummyAbstractSupertype

@TypeName("Supertype")
class ConcreteSubtype1WithValueField extends DummyAbstractSupertype {
    String concreteTypeOneProperty

    ConcreteSubtype1WithValueField(int id, String sharedValue, String concreteTypeOneProperty) {
        super(id, sharedValue, null)
        this.concreteTypeOneProperty = concreteTypeOneProperty
    }

    String getConcreteTypeOneProperty() {
        return concreteTypeOneProperty
    }

    void setConcreteTypeOneProperty(String concreteTypeOneProperty) {
        this.concreteTypeOneProperty = concreteTypeOneProperty
    }
}
