package org.javers.core.model;

public class GenericArrayContainer<T> {
    final private T[] array;

    public GenericArrayContainer(T[] array) {
        this.array = array;
    }

    public T[] getArray() {
        return array;
    }
}
