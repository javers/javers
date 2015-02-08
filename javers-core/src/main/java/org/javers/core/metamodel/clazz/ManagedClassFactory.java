package org.javers.core.metamodel.clazz;

import org.javers.common.validation.Validate;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.PropertyScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class ManagedClassFactory {
    private static final Logger logger = LoggerFactory.getLogger(ManagedClassFactory.class);
    private final PropertyScanner propertyScanner;
    private final ClassAnnotationsScanner classAnnotationsScanner;

    public ManagedClassFactory(PropertyScanner propertyScanner,
                               ClassAnnotationsScanner classAnnotationsScanner) {
        Validate.argumentsAreNotNull(propertyScanner);
        this.propertyScanner = propertyScanner;
        this.classAnnotationsScanner = classAnnotationsScanner;
    }

    public <S> Entity createEntity(Class<S> clazz) {
       return create(new EntityDefinition(clazz));
    }

    public <S> ValueObject createValueObject(Class<S> clazz) {
        return create(new ValueObjectDefinition(clazz));
    }

    public ClientsDomainClass inferFromAnnotations(Class javaClass) {
        List<Property> properties = propertyScanner.scan(javaClass);

        Property foundIdProperty = findIdProperty(properties);

        if (foundIdProperty != null) {
            return new Entity(javaClass, properties, foundIdProperty);
        }

        return create(classAnnotationsScanner.scanAndInfer(javaClass));
    }

    private Property findIdProperty(List<Property> properties) {
        for (Property property : properties)  {
            if (property.looksLikeId()) {
                return property;
            }
        }
        return null;
    }

    public ClientsDomainClass create(ClientsClassDefinition clientsClassDefinition){
        if (clientsClassDefinition instanceof ValueDefinition) {
            return create((ValueDefinition) clientsClassDefinition);
        }
        if (clientsClassDefinition instanceof ValueObjectDefinition) {
            return create((ValueObjectDefinition) clientsClassDefinition);
        }
        if (clientsClassDefinition instanceof EntityDefinition) {
            return create((EntityDefinition) clientsClassDefinition);
        }
        throw new IllegalArgumentException("unsupported "+ clientsClassDefinition);
    }

    public Value create(ValueDefinition valueDefinition) {
         return new Value(valueDefinition.getClazz());
    }

    public Entity create(EntityDefinition entityDefinition) {

        List<Property> properties = propertyScanner.scan(entityDefinition.getClazz());

        Property idProperty = null;
        if (entityDefinition.hasCustomId()){
            idProperty = findIdPropertyByName(properties, entityDefinition);
        }

        List<Property> filteredProperties = filterIgnored(properties, entityDefinition);
        return new Entity(entityDefinition.getClazz(), filteredProperties, idProperty);
    }

    public ValueObject create(ValueObjectDefinition voDefinition) {
        List<Property> properties = propertyScanner.scan(voDefinition.getClazz());
        List<Property> filteredProperties = filterIgnored(properties, voDefinition);
        return new ValueObject(voDefinition.getClazz(), filteredProperties);
    }

    private List<Property> filterIgnored(List<Property> properties, ClientsClassDefinition definition){
        if (definition.getIgnoredProperties().isEmpty()){
            return properties;
        }

        List<Property> filtered = new ArrayList<>(properties);
        for (String ignored : definition.getIgnoredProperties()){
            filterOneProperty(filtered, ignored, definition.getClazz());
        }
        return filtered;
    }

    private void filterOneProperty(List<Property> properties, String ignoredName, Class<?> clientsClass){
        Iterator<Property> it = properties.iterator();
        while(it.hasNext()){
            Property property = it.next();
            if (property.getName().equals(ignoredName)){
                it.remove();
                return;
            }
        }
        throw new JaversException(JaversExceptionCode.PROPERTY_NOT_FOUND, ignoredName, clientsClass.getName());
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

