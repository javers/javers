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
public class BeanBasedManagedClassPropertyScanner extends ManagedClassPropertyScanner {

    private TypeMapper typeMapper;

    public BeanBasedManagedClassPropertyScanner(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
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
