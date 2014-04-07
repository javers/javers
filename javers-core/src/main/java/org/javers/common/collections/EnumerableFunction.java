package org.javers.common.collections;

import org.javers.core.metamodel.object.OwnerContext;

/**
 * @author bartosz walacik
 */
public interface EnumerableFunction<F,T> {
    public T apply(F input, OwnerContext enumerationAwareOwnerContext);
}
