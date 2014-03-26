package org.javers.core.metamodel.object;

import org.javers.common.exception.exceptions.JaversException;
import org.javers.core.metamodel.property.Property;

import static org.javers.common.exception.exceptions.JaversExceptionCode.NOT_IMPLEMENTED;

/**
 * Captured state of client's domain object
 *
 * @author bartosz walacik
 */
public class CdoSnapshot extends Cdo {

    public CdoSnapshot(GlobalCdoId globalId) {
        super(globalId);
    }

    @Override
    public Object getWrappedCdo() {
        throw new JaversException(NOT_IMPLEMENTED);
    }

    @Override
    public Object getPropertyValue(Property property) {
        throw new JaversException(NOT_IMPLEMENTED);
    }
}
