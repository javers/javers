package org.javers.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.javers.core.graph.GraphFactoryHook;

public class HibernateProxyManager implements GraphFactoryHook {

    public <T> T beforeAdd(T entity) {
        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            return (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }
}

