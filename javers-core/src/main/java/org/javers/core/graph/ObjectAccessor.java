package org.javers.core.graph;

import java.util.Optional;

public interface ObjectAccessor<T> {

    Class<T> getTargetClass();

    T access();

    Optional<Object> getLocalId();
}
