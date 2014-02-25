package org.javers.core.metamodel.property;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class ManagedClassFactory {
    private static final Logger logger = LoggerFactory.getLogger(ManagedClassFactory.class);
    private PropertyScanner propertyScanner;

    public ManagedClassFactory(PropertyScanner propertyScanner) {
        Validate.argumentIsNotNull(propertyScanner);
        this.propertyScanner = propertyScanner;
    }

    public <S> Entity createEntity(Class<S> clazz) {
       return create(new EntityDefinition(clazz));
    }

    public <S> ValueObject createValueObject(Class<S> clazz) {
        return create(new ValueObjectDefinition(clazz));
    }

    public ManagedClass infer(Class javaClass) {
        List<Property> properties = propertyScanner.scan(javaClass);

        Property foundIdProperty = findIdProperty(properties);

        if (foundIdProperty != null) {
            logger.info("javersType type of {}(id:{}) inferred as Entity", javaClass.getSimpleName(), foundIdProperty.getName());
            return new Entity(javaClass, properties, foundIdProperty);
        }
        else {
            logger.info("javersType of {} inferred as ValueObject", javaClass.getSimpleName());
            return new ValueObject(javaClass, properties);
        }
    }

    private Property findIdProperty(List<Property> properties) {
        for (Property property : properties)  {
            if (property.looksLikeId()) {
                return property;
            }
        }
        return null;
    }

    public ManagedClass create(ManagedClassDefinition managedClassDefinition){
        if (managedClassDefinition instanceof ValueObjectDefinition) {
            return create((ValueObjectDefinition)managedClassDefinition);
        }
        if (managedClassDefinition instanceof EntityDefinition) {
            return create((EntityDefinition)managedClassDefinition);
        }
        throw new IllegalArgumentException("unsupported "+managedClassDefinition);
    }

    public Entity create(EntityDefinition entityDefinition) {

        List<Property> properties = propertyScanner.scan(entityDefinition.getClazz());

        Property idProperty = null;
        if (entityDefinition.hasCustomId()){
            idProperty = findIdPropertyByName(properties, entityDefinition);
        }

        return new Entity(entityDefinition.getClazz(), properties, idProperty);
    }

    public ValueObject create(ValueObjectDefinition valueObjectDefinition) {
        List<Property> properties = propertyScanner.scan(valueObjectDefinition.getClazz());
        return new ValueObject(valueObjectDefinition.getClazz(), properties);
    }

    private Property findIdPropertyByName(List<Property> beanProperties, EntityDefinition entityDefinition) {
        for (Property property : beanProperties)  {
            if (property.getName().equals( entityDefinition.getIdPropertyName() ) ) {
                return property;
            }
        }
        throw new JaversException(JaversExceptionCode.PROPERTY_NOT_FOUND,entityDefinition.getIdPropertyName(),entityDefinition.getClazz().getName());
    }

}

