package org.javers.core.diff.changetype;

import java.util.Objects;
import java.util.Optional;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

public final class ReferenceAddedChange extends ReferenceChange {

    private final GlobalId right;
    private final transient Optional<Object> rightObject;

    public ReferenceAddedChange(final GlobalId affectedCdoId, final String propertyName,
        final Optional<CommitMetadata> commitMetadata, GlobalId right, Object rightObject) {
        super(affectedCdoId, propertyName, commitMetadata);
        this.right = right;
        this.rightObject = Optional.ofNullable(rightObject);
    }

    @Override
    public GlobalId getLeft() {
        return null;
    }

    @Override
    public GlobalId getRight() {
        return right;
    }

    @Override
    public Optional<Object> getLeftObject() {
        return Optional.empty();
    }

    @Override
    public Optional<Object> getRightObject() {
        return rightObject;
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
            " was added with value " + valuePrinter.formatWithQuotes(getRight());
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
        final ReferenceAddedChange that = (ReferenceAddedChange) o;
        return Objects.equals(right, that.right) &&
            Objects.equals(rightObject, that.rightObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), right, rightObject);
    }
}
