package org.javers.core.diff.changetype;

import java.util.Objects;
import java.util.Optional;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

public class ValueUpdatedChange extends ValueChange {

    private final Atomic left;
    private final Atomic right;

    public ValueUpdatedChange(GlobalId affectedCdoId, String propertyName, Object leftValue, Object rightValue) {
        this(affectedCdoId, propertyName, leftValue, rightValue, Optional.empty());
    }

    public ValueUpdatedChange(GlobalId affectedCdoId, String propertyName, Object leftValue, Object rightValue, Optional<CommitMetadata> commitMetadata) {
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
    public String prettyPrint(PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
            " changed from " + valuePrinter.formatWithQuotes(getLeft()) + " to " +
            valuePrinter.formatWithQuotes(getRight());
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
}
