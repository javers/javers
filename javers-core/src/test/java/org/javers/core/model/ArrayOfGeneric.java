package org.javers.core.model;

/**
 * Unusual structure for testing.
 */
public class ArrayOfGeneric<T> {
    private T[] pool;

    public ArrayOfGeneric(PayloadFactory<T> factory) {
    }

    public interface PayloadFactory<T> {
        T create();
    }
}
