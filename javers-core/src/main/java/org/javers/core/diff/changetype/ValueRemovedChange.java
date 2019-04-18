package org.javers.core.diff.changetype;

import java.util.Optional;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

public class ValueRemovedChange extends ValueChange {

    private final Atomic left;

    public ValueRemovedChange(final GlobalId affectedCdoId,
        final String propertyName, final Object left) {
        this(affectedCdoId, propertyName, Optional.empty(), left);
    }

    public ValueRemovedChange(final GlobalId affectedCdoId, final String propertyName,
        final Optional<CommitMetadata> commitMetadata, final Object left) {
        super(affectedCdoId, propertyName, commitMetadata);
        this.left = new Atomic(left);
    }

    @Override
    public Object getLeft() {
        return left.unwrap();
    }

    @Override
    public Object getRight() {
        return null;
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
            " was removed.";
    }
}
