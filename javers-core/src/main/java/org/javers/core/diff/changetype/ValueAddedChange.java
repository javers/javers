package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Optional;

public class ValueAddedChange extends ValueChange {

    public ValueAddedChange(GlobalId affectedCdoId, String propertyName, Object right) {
        this(affectedCdoId, propertyName, Optional.empty(), right);
    }

    public ValueAddedChange(GlobalId affectedCdoId, String propertyName, Optional<CommitMetadata> commitMetadata, Object right) {
        super(affectedCdoId, propertyName, null, right, commitMetadata);
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) + " was added with value " +
               valuePrinter.formatWithQuotes(getRight());
    }
}
