package org.javers.model.mapping;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class BeanBasedEntityFactory extends EntityFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanBasedEntityFactory.class);

    public BeanBasedEntityFactory(TypeMapper typeMapper) {
        super(typeMapper);
    }

    public <S> Entity<S> create(Class<S> entityClass) {
        typeMapper.registerReferenceType(entityClass);

        List<Method> getters = ReflectionUtil.findAllPersistentGetters(entityClass);
        List<Property> beanProperties = new ArrayList<>();

        for (Method getter : getters) {
            //logger.info("getter: "+getter);

            JaversType javersType = typeMapper.mapType(getter.getReturnType());
            Property beanProperty = new BeanProperty(getter, javersType);
            beanProperties.add(beanProperty);
        }

        return new Entity<S>(entityClass,beanProperties);
    }

}
