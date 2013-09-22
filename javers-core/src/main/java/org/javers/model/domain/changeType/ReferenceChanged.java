package org.javers.model.domain.changeType;

import org.javers.model.domain.Change;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.PropertyChange;
import org.javers.model.mapping.Property;

/**
 * changed reference (in *ToOne relation)
 *
 * @author bartosz walacik
 */
public class ReferenceChanged extends PropertyChange {
    private final Object leftReference;
    private final Object rightReference;

    public ReferenceChanged(GlobalCdoId globalCdoId, Property property, Object leftReference, Object rightReference) {
        super(globalCdoId, property);
        this.leftReference = leftReference;
        this.rightReference = rightReference;
    }

    public Object getLeftReference() {
        return leftReference;
    }

    public Object getRightReference() {
        return rightReference;
    }
}
