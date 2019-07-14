package org.javers.common.collections;

import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;

@FunctionalInterface
public interface EnumerableFunction<F,T> {
    T apply(F input, EnumerationAwareOwnerContext ownerContext);
}
