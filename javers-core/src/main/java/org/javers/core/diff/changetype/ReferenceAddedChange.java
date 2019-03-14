package org.javers.core.diff.changetype;

import java.util.Optional;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

public class ReferenceAddedChange extends PropertyChange {

    private transient GlobalId value;

    public ReferenceAddedChange(final GlobalId affectedCdoId,
        final String propertyName, final Optional<CommitMetadata> commitMetadata,
        final GlobalId value) {
        super(affectedCdoId, propertyName, commitMetadata);
        this.value = value;
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
            " was added with value " +
            valuePrinter.formatWithQuotes(value);
    }

    public GlobalId getValue() {
        return value;
    }
}
