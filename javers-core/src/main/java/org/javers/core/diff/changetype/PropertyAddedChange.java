package org.javers.core.diff.changetype;

import java.util.Optional;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

public class PropertyAddedChange extends PropertyChange {

    private transient Object value;

    public PropertyAddedChange(final GlobalId affectedCdoId,
        final String propertyName, final Object value){
       this(affectedCdoId, propertyName, Optional.empty());
       this.value = value;
    }

    protected PropertyAddedChange(final GlobalId affectedCdoId,
        final String propertyName, final Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, propertyName, commitMetadata);
    }

    @Override
    public String prettyPrint(final PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
            " was added with value " +
            valuePrinter.formatWithQuotes(value);
    }

    public Object getValue() {
        return value;
    }
}
