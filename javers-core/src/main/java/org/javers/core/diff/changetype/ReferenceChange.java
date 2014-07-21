package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

/**
 * changed reference (in *ToOne relation)
 *
 * @author bartosz walacik
 */
public class ReferenceChange extends PropertyChange {
    private final GlobalCdoId left;
    private final GlobalCdoId right;

    public ReferenceChange(GlobalCdoId affectedCdoId, Property property, GlobalCdoId leftReference,
                           GlobalCdoId rightReference) {
        super(affectedCdoId, property);
        this.left = leftReference;
        this.right = rightReference;
    }

    public GlobalCdoId getLeft() {
        return left;
    }

    public GlobalCdoId getRight() {
        return right;
    }
}
