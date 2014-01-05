package org.javers.model.domain.changeType;

import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Property;

/**
 * reference removed from collection (in *ToMany relation)
 *
 * @author bartosz walacik
 */
public class ReferenceRemoved extends PropertyChange {
    private final GlobalCdoId reference;

    public ReferenceRemoved(GlobalCdoId globalCdoId, Property property, GlobalCdoId reference) {
        super(globalCdoId, property);
        this.reference = reference;
    }

    public GlobalCdoId getRemovedReference() {
        return reference;
    }
}
