package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Optional;

public final class ReferenceAddedChange extends ReferenceChange {

    public ReferenceAddedChange(GlobalId affectedCdoId, String propertyName, Optional<CommitMetadata> commitMetadata, GlobalId right, Object rightObject) {
        super(affectedCdoId, propertyName, null, right, null, rightObject, commitMetadata);
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
            " was added with value " + valuePrinter.formatWithQuotes(getRight());
    }
}
