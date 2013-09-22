package org.javers.model.domain.changeType;

import org.javers.model.domain.Change;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.PropertyChange;
import org.javers.model.mapping.Property;

/**
 * reference added to collection (in *ToMany relation)
 *
 * @author bartosz walacik
 */
public class ReferenceAdded extends PropertyChange {
    private final Object reference;

    public ReferenceAdded(GlobalCdoId globalCdoId, Property property, Object reference) {
        super(globalCdoId, property);
        this.reference = reference;
    }

    public Object getAddedReference() {
        return reference;
    }
}
