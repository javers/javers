package org.javers.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.javers.core.graph.ObjectAccessHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUnproxyObjectAccessHook implements ObjectAccessHook {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUnproxyObjectAccessHook.class);

    public <T> T access(T entity) {
        if (entity instanceof HibernateProxy) {
            Hibernate.initialize(entity);
            HibernateProxy proxy = (HibernateProxy) entity;
            T unproxed = (T) proxy.getHibernateLazyInitializer().getImplementation();
            logger.info("unproxying instance of " + entity.getClass().getSimpleName() + " to " + unproxed.getClass().getSimpleName());
            return unproxed;
        }
        return entity;
    }
}

