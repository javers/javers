package org.javers.common.reflection

import javax.persistence.Id

class AbstractGeneric<ID, V> {
    @Id
    private ID id
    private V value

    V getValue() {
        return value
    }

    void setValue(V value) {
        this.value = value
    }
}

class ConcreteWithActualType extends AbstractGeneric<String, List<String>> {
}