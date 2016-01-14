package org.javers.common.reflection;

import javax.persistence.Id;

public class AbstractGeneric<ID, V> {

    @Id
    private ID id;

    private V value;

    public AbstractGeneric(ID id, V value) {
        this.id = id;
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
