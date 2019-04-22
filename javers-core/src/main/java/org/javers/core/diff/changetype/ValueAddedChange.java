package org.javers.core.diff.changetype;

import java.util.Objects;
import java.util.Optional;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

public class ValueAddedChange extends ValueChange {

    private final Atomic right;

    public ValueAddedChange(final GlobalId affectedCdoId,
        final String propertyName, final Object right) {
        this(affectedCdoId, propertyName, Optional.empty(), right);
    }

    public ValueAddedChange(final GlobalId affectedCdoId, final String propertyName,
        final Optional<CommitMetadata> commitMetadata, final Object right) {
        super(affectedCdoId, propertyName, commitMetadata);
        this.right = new Atomic(right);
    }

    @Override
    public Object getLeft() {
        return null;
    }

    @Override
    public Object getRight() {
        return right.unwrap();
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
        final ValueAddedChange that = (ValueAddedChange) o;
        return Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), right);
    }
}
