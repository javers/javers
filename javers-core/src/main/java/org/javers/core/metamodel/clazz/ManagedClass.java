package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Predicate;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

import java.util.*;

import static org.javers.common.exception.JaversExceptionCode.PROPERTY_NOT_FOUND;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Decomposes a class into list of properties.
 * 
 * @author bartosz walacik
 */
public abstract class ManagedClass extends ClientsDomainClass {

    private final Map<String, Property> propertiesByName;
    private final List<Property> managedProperties;
    private final List<Property> transientAnnProperties;

    ManagedClass(Class clientsClass, List<Property> allProperties) {
        super(clientsClass);
        argumentsAreNotNull(allProperties);

        this.managedProperties = new ArrayList<>();
        this.transientAnnProperties = new ArrayList<>();
        this.propertiesByName = new HashMap<>();

        for (Property property : allProperties) {
            if (property.isHasTransientAnn()){
                this.transientAnnProperties.add(property);
            }else {
                this.managedProperties.add(property);
            }

            propertiesByName.put(property.getName(),property);
        }
    }

    /**
     * returns all managed properties
     */
    public List<Property> getProperties() {
        return Collections.unmodifiableList(managedProperties);
    }

    /**
     * returns managed properties subset
     */
    public List<Property> getProperties(Predicate<Property> query) {
        List<Property> retProperties = new ArrayList<>();

        for (Property property : managedProperties) {
            if (query.apply(property)){
                retProperties.add(property);
            }
        }

        return retProperties;
    }

    /**
     * finds property by name (managed or withTransientAnn)
     *
     * @throws JaversException PROPERTY_NOT_FOUND
     */
    public Property getProperty(String withName) {
        Validate.argumentIsNotNull(withName);
        if (!propertiesByName.containsKey(withName)){
            throw new JaversException(PROPERTY_NOT_FOUND, withName, this.getName());
        }
        return propertiesByName.get(withName);
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
