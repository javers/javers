package org.javers.model.mapping;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class BeanBasedEntityFactory extends EntityFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanBasedEntityFactory.class);

    public BeanBasedEntityFactory(TypeMapper typeMapper) {
        super(typeMapper);
    }

    public <S> Entity<S> create(Class<S> beanClass) {


        List<Method> getters = ReflectionUtil.findAllPublicPersistentGetters(beanClass);
        List<Property> beanProperties = new ArrayList<>();

        for (Method getter : getters) {
            //logger.info("getter: "+getter);

            JaversType javersType = typeMapper.mapType(getter.getReturnType());
            Property beanProperty = new BeanProperty(getter, javersType);
            beanProperties.add(beanProperty);
        }
        Entity entity = new Entity(beanClass,beanProperties);

        return entity;
    }

}
