package org.javers.core.diff.changetype;

import org.javers.model.domain.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

/**
 * reference added to collection (in *ToMany relation)
 *
 * @author bartosz walacik
 */
public class ReferenceAdded extends PropertyChange {
    private final GlobalCdoId reference;

    public ReferenceAdded(GlobalCdoId globalCdoId, Property property, GlobalCdoId reference) {
        super(globalCdoId, property);
        this.reference = reference;
    }

    public GlobalCdoId getAddedReference() {
        return reference;
    }
}
