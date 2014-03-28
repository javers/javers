package org.javers.common.collections;

/**
 * @author bartosz walacik
 */
public interface EnumerableFunction<F,T> {
    public T apply(F input, Integer index);
}
