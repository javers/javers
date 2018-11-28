package org.javers.spring.boot.mongo;

import org.javers.core.graph.ObjectAccessHook;
import org.javers.core.graph.ObjectAccessProxy;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;

import java.util.Optional;

public class DBRefUnproxyObjectAccessHook implements ObjectAccessHook<Object> {
    @Override
    public Optional<ObjectAccessProxy<Object>> createAccessor(Object entity) {
        if (entity instanceof LazyLoadingProxy && ((LazyLoadingProxy) entity).toDBRef() != null) {
            return fromObject((LazyLoadingProxy) entity);
        }
        return Optional.empty();
    }

    private Optional<ObjectAccessProxy<Object>> fromObject(LazyLoadingProxy object) {
        return Optional.of(new ObjectAccessProxy(() -> object.getTarget(),
                object.getTarget().getClass(),
                object.toDBRef().getId()));
    }
}

