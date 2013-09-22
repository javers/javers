package org.javers.model.domain.changeType;

import org.javers.model.domain.Change;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.PropertyChange;
import org.javers.model.mapping.Property;

/**
 * reference removed from collection (in *ToMany relation)
 *
 * @author bartosz walacik
 */
public class ReferenceRemoved extends PropertyChange {
    private final Object reference;

    public ReferenceRemoved(GlobalCdoId globalCdoId, Property property, Object reference) {
        super(globalCdoId, property);
        this.reference = reference;
    }

    public Object getRemovedReference() {
        return reference;
    }
}
