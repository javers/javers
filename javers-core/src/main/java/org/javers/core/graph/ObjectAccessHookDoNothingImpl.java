package org.javers.core.graph;

import java.util.Optional;

class ObjectAccessHookDoNothingImpl<T> implements ObjectAccessHook<T> {
    @Override
    public Optional<ObjectAccessProxy<T>> createAccessor(T entity) {
        return Optional.empty();
    }
}
