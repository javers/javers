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

    private static volatile BeanBasedPropertyScanner scanner;

    private BeanBasedPropertyScanner(TypeMapper typeMapper) {
        super(typeMapper);
    }

    public static BeanBasedPropertyScanner getInstane(TypeMapper typeMapper) {
       return  getSingletonInstance(typeMapper);
    }

    private static BeanBasedPropertyScanner getSingletonInstance(TypeMapper typeMapper) {
        if (scanner == null) {
            synchronized (PropertyScanner.class) {
                if (scanner == null) {
                    scanner = new BeanBasedPropertyScanner(typeMapper);
                }
            }
        }
        return scanner;
    }

    @Override
    public List<Property> scan(Class<?> entityClass) {
            List<Method> getters = ReflectionUtil.findAllPersistentGetters(entityClass);
            List<Property> beanProperties = new ArrayList<>();

            for (Method getter : getters) {
                JaversType javersType = typeMapper.getJavesrType(getter.getReturnType());
                Property beanProperty = new BeanProperty(getter, javersType);
                beanProperties.add(beanProperty);
            }
            return beanProperties;
    }
}
