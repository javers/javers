package org.javers.hibernate;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.javers.core.ProxyManager;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;


public class HibernateProxyManager implements ProxyManager {

    public <T> T unproxy(T entity) {
        Hibernate.initialize(entity);
        PropertyDescriptor[] properties = PropertyUtils.getPropertyDescriptors(entity);
        for (PropertyDescriptor propertyDescriptor : properties) {
            try {
                Object origProp = PropertyUtils.getProperty(entity, propertyDescriptor.getName());
                if (origProp instanceof HibernateProxy) {
                    PropertyUtils.setProperty(entity, propertyDescriptor.getName(), ((HibernateProxy) origProp).getHibernateLazyInitializer()
                            .getImplementation());
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

}

