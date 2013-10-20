package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class EntityFactory extends ManagedClassFactory<Entity>{

    public EntityFactory(TypeMapper typeMapper, PropertyScanner propertyScanner) {
        super(typeMapper, propertyScanner);
    }

    public <S> Entity createEntity(Class<S> clazz) {
        return create(clazz);
    }

    @Override
    public <S> Entity create(Class<S> clazz) {
        List<Property> beanProperties = propertyScanner.scan(clazz);
        return new Entity<>(clazz, beanProperties);
    }
}
