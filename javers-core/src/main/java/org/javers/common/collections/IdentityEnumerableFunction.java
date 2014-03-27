package org.javers.common.collections;

/**
 * @author bartosz walacik
 */
public class IdentityEnumerableFunction<T> implements EnumerableFunction<T, T> {
    @Override
    public T apply(T input, int index) {
        return input;
    }
}
