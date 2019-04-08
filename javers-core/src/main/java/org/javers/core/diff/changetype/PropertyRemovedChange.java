package org.javers.core.diff.changetype;

import java.util.Optional;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

public class PropertyRemovedChange extends PropertyChange {

    public PropertyRemovedChange(final GlobalId affectedCdoId,
        final String propertyName){
        this(affectedCdoId, propertyName, Optional.empty());
    }

    protected PropertyRemovedChange(final GlobalId affectedCdoId,
        final String propertyName, final Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, propertyName, commitMetadata);
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
            " was removed.";
    }
}
