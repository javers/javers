package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalId;

import java.util.Objects;

import static org.javers.common.string.ToStringBuilder.addField;

/**
 * @author bartosz walacik
 */
public final class ValueChange extends PropertyChange {
    private final Atomic left;
    private final Atomic right;

    public ValueChange(GlobalId affectedCdoId, String propertyName, Object leftValue, Object rightValue) {
        super(affectedCdoId, propertyName);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ValueChange) {
            ValueChange that = (ValueChange) obj;
            return Objects.equals(this.getAffectedGlobalId(), that.getAffectedGlobalId())
                    && Objects.equals(this.getPropertyName(), that.getPropertyName())
                    && Objects.equals(this.getLeft(), that.getLeft())
                    && Objects.equals(this.getRight(), that.getRight());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAffectedGlobalId(), getPropertyName(), getLeft(), getRight());
    }
}
