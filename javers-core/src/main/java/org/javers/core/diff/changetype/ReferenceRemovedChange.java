package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Optional;

public final class ReferenceRemovedChange extends ReferenceChange {
    public ReferenceRemovedChange(GlobalId affectedCdoId, String propertyName, Optional<CommitMetadata> commitMetadata, GlobalId left, Object leftObject) {
        super(affectedCdoId, propertyName, left, null, leftObject, null, commitMetadata);
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
            " was removed.";
    }
}
