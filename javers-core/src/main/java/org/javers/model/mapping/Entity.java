package org.javers.model.mapping;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.type.ReferenceType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * immutable
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Entity<S> {

    private final Class<S> sourceClass;

    private final List<Property> properties;

    private final Property idProperty;

    public Entity(Class<S> sourceClass, List<Property> properties) {
        argumentIsNotNull(sourceClass);
        argumentIsNotNull(properties);

        this.sourceClass = sourceClass;
        this.properties = properties;
        this.idProperty = findIdProperty();
    }

    private Property findIdProperty() {
        for (Property p : properties) {
            if (p.isId()) {
                return p;
            }
        }
        throw new JaversException(JaversExceptionCode.ENTITY_WITHOUT_ID,sourceClass.getName());
    }

    public boolean isInstance(Object cdo) {
        argumentIsNotNull(cdo);

        return (sourceClass.isAssignableFrom(cdo.getClass()));
    }

    public Property getIdProperty() {
        return idProperty;
    }

    public Class<S> getSourceClass() {
        return sourceClass;
    }

    /**
     * @return list of {@link ReferenceType} properties
     */
    public List<Property> getSingleReferences() {
        List<Property> refProperties = new ArrayList<>();

        for (Property property : properties) {
            if (property.getType() instanceof ReferenceType){
                refProperties.add(property);
            }
        }
        return refProperties;
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