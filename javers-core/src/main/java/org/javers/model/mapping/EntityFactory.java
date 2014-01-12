package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class EntityFactory {

    private PropertyScanner propertyScanner;

    public EntityFactory(PropertyScanner propertyScanner) {
        Validate.argumentIsNotNull(propertyScanner);
        this.propertyScanner = propertyScanner;
    }

    @Deprecated
    public <S> Entity create(Class<S> clazz) {
       return create(new EntityDefinition(clazz));
    }

    public Entity create(EntityDefinition entityDefinition) {

        List<Property> beanProperties = propertyScanner.scan(entityDefinition.getClazz());

        Property idProperty = null;
        if (entityDefinition.hasCustomId()){
            idProperty = findIdPropertyByName(beanProperties, entityDefinition);
        }

        return new Entity(entityDefinition.getClazz(), beanProperties, idProperty);
    }

    private Property findIdPropertyByName(List<Property> beanProperties, EntityDefinition entityDefinition) {
        for (Property property : beanProperties)  {
            if (property.getName().equals( entityDefinition.getIdPropertyName() ) ) {
                return property;
            }
        }
        throw new JaversException(JaversExceptionCode.PROPERTY_NOT_FOUND,entityDefinition.getIdPropertyName(),entityDefinition.getClazz().getName());
    }

    public ValueObject create(ValueObjectDefinition valueObjectDefinition) {
        List<Property> beanProperties = propertyScanner.scan(valueObjectDefinition.getClazz());

        return new ValueObject(valueObjectDefinition.getClazz(), beanProperties);
    }
}

