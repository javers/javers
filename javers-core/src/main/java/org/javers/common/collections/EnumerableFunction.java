package org.javers.common.collections;

import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;

/**
 * @author bartosz walacik
 */
public interface EnumerableFunction<F,T> {
    T apply(F input, EnumerationAwareOwnerContext enumeration);
}
