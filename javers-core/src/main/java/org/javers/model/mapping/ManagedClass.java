package org.javers.model.mapping;

import org.javers.model.mapping.type.CollectionType;
import org.javers.model.mapping.type.EntityReferenceType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public abstract class ManagedClass<S> {

    protected final Class<S> sourceClass;

    protected final List<Property> properties;

    public ManagedClass(Class<S> sourceClass, List<Property> properties) {
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

    /**
     * @return list of {@link org.javers.model.mapping.type.EntityReferenceType} properties
     */
    public List<Property> getSingleReferences() {
        List<Property> refProperties = new ArrayList<>();

        for (Property property : properties) {
            if (property.getType() instanceof EntityReferenceType){
                refProperties.add(property);
            }
        }
        return refProperties;
    }

    public List<Property> getMultiReferences() {
        List<Property> refProperties = new ArrayList<>();

        for (Property property : properties) {
            if (property.getType() instanceof CollectionType){
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
        return sourceClass.equals((ManagedClass)obj) && properties.equals(((ManagedClass) obj).getProperties());
    }

}
