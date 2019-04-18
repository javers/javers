package org.javers.core.diff.changetype;

import java.util.Objects;
import java.util.Optional;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

public final class ReferenceUpdatedChange extends ReferenceChange {

    private final GlobalId left;
    private final GlobalId right;
    private final transient Optional<Object> leftObject;
    private final transient Optional<Object> rightObject;

    public ReferenceUpdatedChange(final GlobalId affectedCdoId,
        final String propertyName, final GlobalId leftReference,
        final GlobalId rightReference, final Object leftObject,
        final Object rightObject, final Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, propertyName, commitMetadata);
        this.left = leftReference;
        this.right = rightReference;
        this.leftObject = Optional.ofNullable(leftObject);
        this.rightObject = Optional.ofNullable(rightObject);
    }

    public ReferenceUpdatedChange(final GlobalId affectedCdoId, final String propertyName,
        final GlobalId left, final GlobalId right, final Object leftObject,
        final Object rightObject) {
        this(affectedCdoId, propertyName, left, right, leftObject, rightObject, Optional.empty());
    }

    @Override
    public GlobalId getLeft() {
        return left;
    }

    @Override
    public GlobalId getRight() {
        return right;
    }

    @Override
    public Optional<Object> getLeftObject() {
        return leftObject;
    }

    @Override
    public Optional<Object> getRightObject() {
        return rightObject;
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
            " changed from " + valuePrinter.formatWithQuotes(getLeft()) + " to " +
            valuePrinter.formatWithQuotes(getRight());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final ReferenceUpdatedChange that = (ReferenceUpdatedChange) o;
        return Objects.equals(left, that.left) &&
            Objects.equals(right, that.right) &&
            Objects.equals(leftObject, that.leftObject) &&
            Objects.equals(rightObject, that.rightObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), left, right, leftObject, rightObject);
    }
}
