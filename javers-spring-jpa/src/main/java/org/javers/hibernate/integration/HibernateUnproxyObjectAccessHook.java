package org.javers.hibernate.integration;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer;
import org.javers.core.graph.ObjectAccessHook;
import org.javers.core.graph.ObjectAccessProxy;

import java.util.Optional;

public class HibernateUnproxyObjectAccessHook<T> implements ObjectAccessHook<T> {

    @Override
    public Optional<ObjectAccessProxy<T>> createAccessor(T entity) {
        if (entity instanceof HibernateProxy) {
            LazyInitializer lazyInitializer = ((HibernateProxy) entity).getHibernateLazyInitializer();

            return fromLazyInitializer(lazyInitializer);
        }
        if (entity instanceof JavassistLazyInitializer){
            JavassistLazyInitializer proxy = (JavassistLazyInitializer) entity;
            return fromLazyInitializer(proxy);
        }

        return Optional.empty();
    }

    private Optional<ObjectAccessProxy<T>> fromLazyInitializer(LazyInitializer lazyInitializer) {
        return Optional.of(new ObjectAccessProxy(() -> lazyInitializer.getImplementation(),
                lazyInitializer.getPersistentClass(),
                lazyInitializer.getIdentifier()));
    }
}

