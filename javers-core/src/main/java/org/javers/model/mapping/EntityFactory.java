package org.javers.model.mapping;

import org.javers.common.validation.Validate;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class EntityFactory extends ManagedClassFactory<Entity>{

    private PropertyScanner propertyScanner;

    public EntityFactory(PropertyScanner propertyScanner) {
        Validate.argumentIsNotNull(propertyScanner);
        this.propertyScanner = propertyScanner;
    }

    @Override
    public <S> Entity create(Class<S> clazz) {
        List<Property> beanProperties = propertyScanner.scan(clazz);
        return new Entity<>(clazz, beanProperties);
    }
}
