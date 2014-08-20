package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

/**
 * changed reference (in *ToOne relation)
 *
 * @author bartosz walacik
 */
public class ReferenceChange extends PropertyChange {
    private final GlobalId left;
    private final GlobalId right;

    public ReferenceChange(GlobalId affectedCdoId, Property property, GlobalId leftReference,
                           GlobalId rightReference) {
        super(affectedCdoId, property);
        this.left = leftReference;
        this.right = rightReference;
    }

    public GlobalId getLeft() {
        return left;
    }

    public GlobalId getRight() {
        return right;
    }
}
