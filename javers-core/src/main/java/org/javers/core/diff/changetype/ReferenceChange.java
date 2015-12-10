package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import static org.javers.common.string.ToStringBuilder.addField;

/**
 * changed reference (in *ToOne relation)
 *
 * @author bartosz walacik
 */
public final class ReferenceChange extends PropertyChange {
    private final GlobalId left;
    private final GlobalId right;

    public ReferenceChange(GlobalId affectedCdoId, String propertyName, GlobalId leftReference,
                           GlobalId rightReference) {
        super(affectedCdoId, propertyName);
        this.left = leftReference;
        this.right = rightReference;
    }

    public GlobalId getLeft() {
        return left;
    }

    public GlobalId getRight() {
        return right;
    }

    @Override
    protected String fieldsToString() {
        return super.fieldsToString() + addField("oldRef", left) + addField("newRef", right);
    }
}
