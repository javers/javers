package org.javers.core.metamodel.object;

import org.javers.core.metamodel.property.Property;

import static org.javers.common.validation.Validate.argumentCheck;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Wrapper for live client's domain object (aka CDO)
 *
 * @author bartosz walacik
 */
public class CdoWrapper extends Cdo {
    private final Object wrappedCdo;

    public CdoWrapper(Object wrappedCdo, GlobalCdoId globalId) {
        super(globalId);
        argumentsAreNotNull(wrappedCdo);
        argumentCheck(globalId.getCdoClass().isInstance(wrappedCdo), "wrappedCdo is not an instance of given managedClass");

        this.wrappedCdo = wrappedCdo;
    }

    /**
     * never returns null
     */
    @Override
    public Object getWrappedCdo() {
        return wrappedCdo;
    }

    @Override
    public Object getPropertyValue(Property property) {
        return property.get(wrappedCdo);
    }
}
