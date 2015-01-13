package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import static org.javers.common.string.ToStringBuilder.addField;

/**
 * @author bartosz walacik
 */
public class ValueChange extends PropertyChange {
    private final Atomic left;
    private final Atomic right;

    public ValueChange(GlobalId affectedCdoId, Property property, Object leftValue, Object rightValue) {
        super(affectedCdoId, property);
        this.left = new Atomic(leftValue);
        this.right = new Atomic(rightValue);
    }

    public Object getLeft() {
        return left.unwrap();
    }

    public Object getRight() {
        return right.unwrap();
    }

    @Override
    protected String fieldsToString() {
        return super.fieldsToString() + addField("oldVal", getLeft()) + addField("newVal", getRight());
    }
}
