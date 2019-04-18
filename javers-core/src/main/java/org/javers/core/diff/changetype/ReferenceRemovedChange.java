package org.javers.core.diff.changetype;

import java.util.Objects;
import java.util.Optional;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

public final class ReferenceRemovedChange extends ReferenceChange {

    private final GlobalId left;

    private final transient Optional<Object> leftObject;

    public ReferenceRemovedChange(final GlobalId affectedCdoId, final String propertyName,
        final Optional<CommitMetadata> commitMetadata, final GlobalId left, final Object leftObject) {
        super(affectedCdoId, propertyName, commitMetadata);
        this.left = left;
        this.leftObject = Optional.ofNullable(leftObject);
    }

    @Override
    public GlobalId getLeft() {
        return left;
    }

    @Override
    public GlobalId getRight() {
        return null;
    }

    @Override
    public Optional<Object> getLeftObject() {
        return leftObject;
    }

    @Override
    public Optional<Object> getRightObject() {
        return Optional.empty();
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
            " was removed.";
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
        final ReferenceRemovedChange that = (ReferenceRemovedChange) o;
        return Objects.equals(left, that.left) &&
            Objects.equals(leftObject, that.leftObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), left, leftObject);
    }
}
