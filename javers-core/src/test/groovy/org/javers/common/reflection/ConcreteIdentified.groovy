package org.javers.common.reflection

class AbstractIdentified<ID> {
    ID id

    ID getId() {
        return id
    }
}

class Versioned<ID, VER> extends AbstractIdentified<ID> {
    VER version
}

class ConcreteIdentified extends Versioned<Long, Long> {
    String name
}
