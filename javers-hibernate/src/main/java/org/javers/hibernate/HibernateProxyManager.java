package org.javers.hibernate;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.javers.core.graph.GraphFactoryHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;


public class HibernateProxyManager implements GraphFactoryHook {

    private static final Logger logger = LoggerFactory.getLogger(HibernateProxyManager.class);

    public <T> T beforeAdd(T entity) {
        Hibernate.initialize(entity);
        PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(entity);
        for (PropertyDescriptor propertyDescriptor : properties) {
            try {
                Object originalProperty = PropertyUtils.getProperty(entity, propertyDescriptor.getName());
                if (originalProperty instanceof HibernateProxy) {
                    PropertyUtils.setProperty(entity, propertyDescriptor.getName(),
                            ((HibernateProxy) originalProperty).getHibernateLazyInitializer().getImplementation());
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error("Could not detect entity properties using PropertyUtils", e.getCause());
            }
        }
        return entity;
    }
}

