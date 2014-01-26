package org.javers.core.metamodel.property;

import org.javers.common.reflection.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class BeanBasedPropertyScanner implements PropertyScanner {

    @Override
    public List<Property> scan(Class<?> managedClass) {
            List<Method> getters = ReflectionUtil.findAllPersistentGetters(managedClass);
            List<Property> beanProperties = new ArrayList<>();

            for (Method getter : getters) {
                Property beanProperty = new BeanProperty(getter);
                beanProperties.add(beanProperty);
            }
            return beanProperties;
    }
}
