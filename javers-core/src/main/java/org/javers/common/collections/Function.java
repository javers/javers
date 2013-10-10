package org.javers.common.collections;

/**
 * @author Maciej Zasada
 */
public interface Function<F, T> {

    T apply(F input);
}
