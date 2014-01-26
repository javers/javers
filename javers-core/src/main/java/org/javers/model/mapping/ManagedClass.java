package org.javers.model.mapping;

import org.javers.common.collections.Predicate;
import org.javers.core.metamodel.property.Property;

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

    protected final Class sourceClass;
    protected final List<Property> properties;

    public ManagedClass(Class sourceClass, List<Property> properties) {
        argumentsAreNotNull(sourceClass, properties);
        this.sourceClass = sourceClass;
        this.properties = new ArrayList<>(properties);
    }

    public boolean isInstance(Object cdo) {
        argumentIsNotNull(cdo);
        return (sourceClass.isAssignableFrom(cdo.getClass()));
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    @Override
    public int hashCode() {
        return sourceClass.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        ManagedClass other = (ManagedClass)obj;
        return sourceClass.equals(other.sourceClass);
    }

    /**
     * shortcut to {@link Class#getName()}
     */
    public String getName() {
        return sourceClass.getName();
    }

    public List<Property> getCollectionTypeProperties() {
        return getProperties(new Predicate<Property>() {
            public boolean apply(Property property) {
                return (Collection.class.isAssignableFrom(property.getType()));
            }
        });
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
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
}
