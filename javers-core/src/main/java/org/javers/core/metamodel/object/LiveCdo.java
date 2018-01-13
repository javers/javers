package org.javers.core.metamodel.object;

import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.ManagedType;

import java.util.Optional;

import static org.javers.common.validation.Validate.*;

/**
 * Wrapper for live client's domain object (aka CDO)
 *
 * @author bartosz walacik
 */
public abstract class LiveCdo extends Cdo {

    public LiveCdo(GlobalId globalId, ManagedType managedType) {
        super(globalId, managedType);
    }

    @Override
    public Object getPropertyValue(String propertyName) {
        argumentIsNotNull(propertyName);
        Property property = getManagedType().getProperty(propertyName);
        return getPropertyValue(property);
    }

    @Override
    public Object getPropertyValue(Property property) {
        argumentIsNotNull(property);
        return property.get(wrappedCdo());
    }

    /**
     * never returns empty
     */
    @Override
    public Optional<Object> getWrappedCdo() {
        return Optional.of(wrappedCdo());
    }

    @Override
    public boolean isNull(Property property) {
        argumentIsNotNull(property);
        return property.isNull(wrappedCdo());
    }

    protected abstract Object wrappedCdo();
}
