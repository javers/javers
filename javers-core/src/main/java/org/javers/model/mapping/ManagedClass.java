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
