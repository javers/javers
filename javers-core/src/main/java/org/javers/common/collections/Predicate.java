package org.javers.common.collections;

/**
 * @author bartosz walacik
 */
public interface Predicate<T> {
    boolean apply(T input);
}
