package org.javers.model.mapping;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class BeanBasedPropertyScanner extends PropertyScanner {

    public BeanBasedPropertyScanner(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public List<Property> scan(Class<?> managedClass) {
            List<Method> getters = ReflectionUtil.findAllPersistentGetters(managedClass);
            List<Property> beanProperties = new ArrayList<>();

            for (Method getter : getters) {
                JaversType javersType = typeMapper.getJavesrType(getter.getGenericReturnType());
                Property beanProperty = new BeanProperty(getter, javersType);
                beanProperties.add(beanProperty);
            }
            return beanProperties;
    }
}
