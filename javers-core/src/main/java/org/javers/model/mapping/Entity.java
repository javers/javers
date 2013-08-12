package org.javers.model.mapping;

import org.javers.common.validation.Validate;

import java.util.Collections;
import java.util.List;

import static org.javers.common.validation.Validate.*;

/**
 * immutable
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Entity<S> {

    private final Class<S> sourceClass;

    private final List<Property> properties;

    public Entity(Class<S> sourceClass, List<Property> properties) {
        argumentIsNotNull(sourceClass);
        argumentIsNotNull(properties);

        this.sourceClass = sourceClass;
        this.properties = properties;
    }

    public boolean isInstance(Object cdo) {
        argumentIsNotNull(cdo);

        return (sourceClass.isAssignableFrom(cdo.getClass()));
    }

    public Class<S> getSourceClass() {
        return sourceClass;
    }

    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
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
    public int hashCode() {
        return sourceClass.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Entity)) {
            return false;
        }
        return sourceClass.equals((Entity)obj) && properties.equals(((Entity) obj).getProperties());
    }
}