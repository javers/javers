package org.javers.core.diff.changetype;

import org.javers.model.domain.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

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
