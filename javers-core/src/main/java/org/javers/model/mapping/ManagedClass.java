package org.javers.model.mapping;

import org.javers.model.mapping.type.JaversType;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Object type that can be managed by Javers,
 * reflects one class in clients data model.
 *
 * Holds list of properties, see {@link JaversType}
 *
 * @author bartosz walacik
 */
public abstract class ManagedClass<S> {

    protected final Class<S> sourceClass;

    public ManagedClass(Class<S> sourceClass) {
        argumentIsNotNull(sourceClass);

        this.sourceClass = sourceClass;
    }

    public boolean isInstance(Object cdo) {
        argumentIsNotNull(cdo);

        return (sourceClass.isAssignableFrom(cdo.getClass()));
    }

    public Class<S> getSourceClass() {
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

}
