package org.javers.core.diff.changetype;

import org.javers.model.domain.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

/**
 * changed reference (in *ToOne relation)
 *
 * @author bartosz walacik
 */
public class ReferenceChange extends PropertyChange {
    private final GlobalCdoId leftReference;
    private final GlobalCdoId rightReference;

    public ReferenceChange(GlobalCdoId globalCdoId, Property property, GlobalCdoId leftReference,
                           GlobalCdoId rightReference) {
        super(globalCdoId, property);
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
