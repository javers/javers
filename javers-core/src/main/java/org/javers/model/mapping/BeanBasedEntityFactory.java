package org.javers.model.mapping;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class BeanBasedEntityFactory extends EntityFactory {

    public BeanBasedEntityFactory(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public <S> Entity<S> createEntity(Class<S> entityClass) {
        typeMapper.registerReferenceType(entityClass);
        List<Property> beanProperties = getManagedClassProperties(entityClass);
        return new Entity<S>(entityClass,beanProperties);
    }

    private <S> List<Property> getManagedClassProperties(Class<S> entityClass) {
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
