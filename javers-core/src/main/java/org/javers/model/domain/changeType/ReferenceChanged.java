package org.javers.model.domain.changeType;

import org.javers.model.domain.Diff;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.PropertyChange;
import org.javers.model.mapping.Property;

/**
 * changed reference (in *ToOne relation)
 *
 * @author bartosz walacik
 */
public class ReferenceChanged extends PropertyChange {
    private final GlobalCdoId leftReference;
    private final GlobalCdoId rightReference;

    public ReferenceChanged(GlobalCdoId globalCdoId, Diff parent, Property property, GlobalCdoId leftReference, GlobalCdoId rightReference) {
        super(globalCdoId, parent, property);
        this.leftReference = leftReference;
        this.rightReference = rightReference;
    }

    public GlobalCdoId getLeftReference() {
        return leftReference;
    }

    public GlobalCdoId getRightReference() {
        return rightReference;
    }
}
