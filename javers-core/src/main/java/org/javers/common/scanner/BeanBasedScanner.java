package org.javers.common.scanner;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.model.mapping.BeanProperty;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class BeanBasedScanner extends Scanner {

    private static volatile BeanBasedScanner scanner;

    private BeanBasedScanner(TypeMapper typeMapper) {
        super(typeMapper);
    }

    public static BeanBasedScanner getInstane(TypeMapper typeMapper) {
       return  getSingletonInstance(typeMapper);
    }

    private static BeanBasedScanner getSingletonInstance(TypeMapper typeMapper) {
        if (scanner == null) {
            synchronized (Scanner.class) {
                if (scanner == null) {
                    scanner = new BeanBasedScanner(typeMapper);
                }
            }
        }
        return scanner;
    }

    @Override
    public <S> List<Property> scan(Class<S> entityClass) {
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
