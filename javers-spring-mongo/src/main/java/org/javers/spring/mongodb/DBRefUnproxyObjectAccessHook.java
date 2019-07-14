package org.javers.spring.mongodb;

import org.javers.core.graph.ObjectAccessHook;
import org.javers.core.graph.ObjectAccessProxy;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;

import java.util.Optional;

public class DBRefUnproxyObjectAccessHook implements ObjectAccessHook<Object> {
    @Override
    public Optional<ObjectAccessProxy<Object>> createAccessor(Object entity) {
        if (entity instanceof LazyLoadingProxy) {
            LazyLoadingProxy proxy = (LazyLoadingProxy) entity;

            return Optional.of(new ObjectAccessProxy(() -> proxy.getTarget(),
                    proxy.getTarget().getClass(),
                    proxy.toDBRef().getId().toString()));
        }
        return Optional.empty();
    }
}

