package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class BeanBasedEntityFactory extends EntityFactory {

    public BeanBasedEntityFactory(TypeMapper typeMapper) {
        super(typeMapper, BeanBasedPropertyScanner.getInstane(typeMapper));
    }

    @Override
    public <S> Entity<S> create(Class<S> entityClass) {
        typeMapper.registerReferenceType(entityClass);
        List<Property> beanProperties = propertyScanner.scan(entityClass);
        return new Entity<>(entityClass,beanProperties);
    }
}
