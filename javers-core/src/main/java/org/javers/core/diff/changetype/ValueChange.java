package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.MissingProperty;

import java.util.Objects;
import java.util.Optional;

/**
 * Change on a Value property, like int or String
 *
 * @author bartosz walacik
 */
public class ValueChange extends PropertyChange {
    private final Atomic left;
    private final Atomic right;

    public static ValueChange create(GlobalId affectedCdoId, String propertyName, Object leftValue, Object rightValue) {
        if (MissingProperty.INSTANCE == leftValue) {
            return new ValueAddedChange(affectedCdoId, propertyName, rightValue, Optional.empty());
        }
        if (MissingProperty.INSTANCE == rightValue) {
            return new ValueRemovedChange(affectedCdoId, propertyName, leftValue, Optional.empty());

        }
        return new ValueChange(affectedCdoId, propertyName, leftValue, rightValue, Optional.empty());
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

    public static class ValueRemovedChange extends ValueChange {

        public ValueRemovedChange(GlobalId affectedCdoId, String propertyName, Object left, Optional<CommitMetadata> commitMetadata) {
            super(affectedCdoId, propertyName, left, null, commitMetadata);
        }

        @Override
        public String prettyPrint(final PrettyValuePrinter valuePrinter) {
            Validate.argumentIsNotNull(valuePrinter);

            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) + " was removed, value " +
                   valuePrinter.formatWithQuotes(getLeft());
        }
    }

    public static class ValueAddedChange extends ValueChange {

        public ValueAddedChange(GlobalId affectedCdoId, String propertyName, Object right, Optional<CommitMetadata> commitMetadata) {
            super(affectedCdoId, propertyName, null, right, commitMetadata);
        }

        @Override
        public String prettyPrint(final PrettyValuePrinter valuePrinter) {
            Validate.argumentIsNotNull(valuePrinter);

            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) + " was added with value " +
                   valuePrinter.formatWithQuotes(getRight());
        }
    }
}
