package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Predicate;
import org.javers.common.exception.JaversException;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.common.exception.JaversExceptionCode.PROPERTY_NOT_FOUND;
import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Decomposes a class into list of properties.
 * 
 * @author bartosz walacik
 */
public abstract class ManagedClass extends ClientsDomainClass {
    
    private final List<Property> properties;
    private final List<Property> propertiesUnmodifiable;

    ManagedClass(Class clientsClass, List<Property> properties) {
        super(clientsClass);
        argumentsAreNotNull(properties);
        this.properties = new ArrayList<>(properties);
        this.propertiesUnmodifiable = Collections.unmodifiableList(properties);
    }

    public List<Property> getProperties() {
        return propertiesUnmodifiable;
    }

    public List<Property> getProperties(Predicate<Property> query) {
        List<Property> retProperties = new ArrayList<>();

        for (Property property : properties) {
            if (query.apply(property)){
                retProperties.add(property);
            }
        }

        return retProperties;
    }

    /**
     * @throws JaversException PROPERTY_NOT_FOUND
     */
    public Property getProperty(String withName) {
        for (Property property : properties) {
            if (property.getName().equals(withName)) {
                return property;
            }
        }
        throw new JaversException(PROPERTY_NOT_FOUND, withName, this.getName());
    }

    public boolean hasProperty(String withName){
        try{
            getProperty(withName);
            return true;
        } catch (JaversException e){
            return false;
        }
    }
}
