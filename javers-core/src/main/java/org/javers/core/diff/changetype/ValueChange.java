package org.javers.core.diff.changetype;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;
import java.util.Objects;
import java.util.Optional;

import static org.javers.common.string.ToStringBuilder.addField;

/**
 * @author bartosz walacik
 */
public final class ValueChange extends PropertyChange {
    private final Atomic left;
    private final Atomic right;

    public ValueChange(GlobalId affectedCdoId, String propertyName, Object leftValue, Object rightValue) {
        this(affectedCdoId, propertyName, leftValue, rightValue, Optional.empty());
    }

    public ValueChange(GlobalId affectedCdoId, String propertyName, Object leftValue, Object rightValue, Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, propertyName, commitMetadata);
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
            return super.equals(that)
                    && Objects.equals(this.getLeft(), that.getLeft())
                    && Objects.equals(this.getRight(), that.getRight());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLeft(), getRight());
    }

    @Override
    public String toString() {
        return "value changed on '"+ getPropertyName()+"' property: '"+ getLeft() + "' -> '" + getRight() + "'";
    }
}
