package org.javers.model.mapping;

import org.javers.model.mapping.type.CollectionType;
import org.javers.model.mapping.type.EntityReferenceType;
import org.javers.model.mapping.type.ValueObjectType;

import java.util.ArrayList;
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

    /**
     */
    public List<Property> getSingleReferences() {
        List<Property> refProperties = new ArrayList<>();

        for (Property property : properties) {
            if (property.getType() instanceof EntityReferenceType ||
                property.getType() instanceof ValueObjectType ){
                refProperties.add(property);
            }
        }
        return refProperties;
    }

    public List<Property> getCollectionTypeProperties() {
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
}
