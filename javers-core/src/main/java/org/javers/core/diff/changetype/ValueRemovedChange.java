package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Optional;

public class ValueRemovedChange extends ValueChange {

    public ValueRemovedChange(final GlobalId affectedCdoId, final String propertyName, final Object left) {
        this(affectedCdoId, propertyName, Optional.empty(), left);
    }

    public ValueRemovedChange(final GlobalId affectedCdoId, final String propertyName, final Optional<CommitMetadata> commitMetadata, final Object left) {
        super(affectedCdoId, propertyName, left, null, commitMetadata);
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) + " was removed.";
    }
}
