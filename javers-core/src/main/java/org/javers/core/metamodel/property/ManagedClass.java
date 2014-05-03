package org.javers.core.metamodel.property;

import org.javers.common.collections.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Object type that can be managed by Javers,
 * reflects one class in clients domain model.
 * <p/>
 * ManagedClass instances are called here <b>Cdo</b> - client's domain objects
 *
 * @author bartosz walacik
 */
public abstract class ManagedClass {

    private final Class sourceClass;
    private final List<Property> properties;
    private final List<Property> propertiesUnmodifiable;

    ManagedClass(Class sourceClass, List<Property> properties) {
        argumentsAreNotNull(sourceClass, properties);
        this.sourceClass = sourceClass;
        this.properties = new ArrayList<>(properties);
        this.propertiesUnmodifiable = Collections.unmodifiableList(properties);
    }

    public boolean isInstance(Object cdo) {
        argumentIsNotNull(cdo);
        return (sourceClass.isAssignableFrom(cdo.getClass()));
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    /**
     * shortcut to {@link Class#getName()}
     */
    public String getName() {
        return sourceClass.getName();
    }

    /**
     * 'Entity' or 'ValueObject'
     */
    public String getSimpleName() {
        return this.getClass().getSimpleName();
    }

    public List<Property> getCollectionTypeProperties() {
        return getProperties(new Predicate<Property>() {
            public boolean apply(Property property) {
                return (Collection.class.isAssignableFrom(property.getType()));
            }
        });
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

    public Property getProperty(String withName) {
        Property found = null;
        for (Property property : properties) {
            if (property.getName().equals(withName)) {
                found = property;
            }
        }
        return found;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || !(o instanceof ManagedClass)) {return false;}

        ManagedClass that = (ManagedClass) o;
        return sourceClass.equals(that.sourceClass);
    }

    @Override
    public int hashCode() {
        return sourceClass.hashCode();
    }
}
