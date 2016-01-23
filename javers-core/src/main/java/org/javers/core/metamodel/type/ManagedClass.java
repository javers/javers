package org.javers.core.metamodel.type;

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
class ManagedClass {
    private final Class<?> baseJavaClass;
    private final Map<String, Property> propertiesByName;
    private final List<Property> managedProperties;
    private final List<Property> looksLikeId;

    ManagedClass(Class baseJavaClass, List<Property> allProperties, List<Property> looksLikeId) {
        argumentsAreNotNull(baseJavaClass, allProperties, looksLikeId);

        this.baseJavaClass = baseJavaClass;
        this.managedProperties = new ArrayList<>();
        this.propertiesByName = new HashMap<>();
        this.looksLikeId = looksLikeId;

        for (Property property : allProperties) {
            if (!property.hasTransientAnn()){
                this.managedProperties.add(property);
            }
            propertiesByName.put(property.getName(),property);
        }
    }

    /**
     * returns all managed properties
     */
    List<Property> getManagedProperties() {
        return Collections.unmodifiableList(managedProperties);
    }

    List<Property> getLooksLikeId() {
        return Collections.unmodifiableList(looksLikeId);
    }

    Set<String> getPropertyNames(){
        return Collections.unmodifiableSet(propertiesByName.keySet());
    }

    /**
     * returns managed properties subset
     */
    List<Property> getManagedProperties(Predicate<Property> query) {
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
    Property getProperty(String withName) {
        Validate.argumentIsNotNull(withName);
        if (!propertiesByName.containsKey(withName)){
            throw new JaversException(PROPERTY_NOT_FOUND, withName, baseJavaClass.getName());
        }
        return propertiesByName.get(withName);
    }

    Class<?> getBaseJavaClass() {
        return baseJavaClass;
    }
}
