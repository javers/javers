package org.javers.core.metamodel.object;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.ManagedType;

import static org.javers.common.validation.Validate.argumentCheck;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Wrapper for live client's domain object (aka CDO)
 *
 * @author bartosz walacik
 */
public class CdoWrapper extends Cdo {
    private final Object wrappedCdo;
    private final ManagedType managedType;

    public CdoWrapper(Object wrappedCdo, GlobalId globalId, ManagedType managedType) {
        super(globalId);
        argumentsAreNotNull(wrappedCdo, managedType);
        argumentCheck(managedType.isInstance(wrappedCdo), "wrappedCdo is not an instance of given managedClass");

        this.wrappedCdo = wrappedCdo;
        this.managedType = managedType;
    }

    public ManagedType getManagedType() {
        return managedType;
    }

    @Override
    public Object getPropertyValue(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        Property property = managedType.getProperty(propertyName);
        return property.get(wrappedCdo);
    }

    /**
     * never returns empty
     */
    @Override
    public Optional<Object> getWrappedCdo() {
        return Optional.of(wrappedCdo);
    }

    @Override
    public boolean isNull(Property property) {
        return property.isNull(wrappedCdo);
    }
}
