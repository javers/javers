package org.javers.common.reflection

import javax.persistence.Id

class AbstractGeneric<ID, V> {
    @Id
    ID id
    V value

    V getValue() {
        return value
    }

    void setValue(V value) {
        this.value = value
    }
}

class ConcreteWithActualType extends AbstractGeneric<String, List<String>> {
    ConcreteWithActualType(String id, List<String> value) {
        this.id = id
        this.value = value
    }
}