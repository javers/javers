package org.javers.common.reflection;

public class AbstractGeneric<T> {
    private T value;

    public AbstractGeneric(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
