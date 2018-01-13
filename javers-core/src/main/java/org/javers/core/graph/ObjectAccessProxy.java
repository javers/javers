package org.javers.core.graph;

import org.javers.common.validation.Validate;
import java.util.Optional;
import java.util.function.Supplier;

public class ObjectAccessProxy<T> implements ObjectAccessor<T> {
    private final Class<T> javaClass;
    private final Object localId;
    private final Supplier<T> objectSupplier;

    public ObjectAccessProxy(Supplier<T> objectSupplier, Class<T> javaClass, Object localId) {
        Validate.argumentsAreNotNull(objectSupplier, javaClass);
        this.javaClass = javaClass;
        this.objectSupplier = objectSupplier;
        this.localId = localId;
    }

    @Override
    public Class getTargetClass() {
        return javaClass;
    }

    @Override
    public Optional<Object> getLocalId() {
        return Optional.ofNullable(localId);
    }

    @Override
    public T access() {
        return objectSupplier.get();
    }

    public Supplier<T> getObjectSupplier() {
        return objectSupplier;
    }
}
