package org.javers.model.domain.changeType;

import org.javers.model.domain.Diff;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.PropertyChange;
import org.javers.model.mapping.Property;

/**
 * reference added to collection (in *ToMany relation)
 *
 * @author bartosz walacik
 */
public class ReferenceAdded extends PropertyChange {
    private final GlobalCdoId reference;

    public ReferenceAdded(GlobalCdoId globalCdoId, Diff parent, Property property, GlobalCdoId reference) {
        super(globalCdoId, parent, property);
        this.reference = reference;
    }

    public GlobalCdoId getAddedReference() {
        return reference;
    }
}
