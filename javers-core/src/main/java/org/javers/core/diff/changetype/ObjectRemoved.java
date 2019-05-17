package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Optional;

/**
 * Object removed from a graph
 *
 * @author bartosz walacik
 */
public final class ObjectRemoved extends Change {

    ObjectRemoved(GlobalId removed, Optional<Object> removedCdo) {
        this(removed, removedCdo, Optional.empty());
    }

    public ObjectRemoved(GlobalId removed, Optional<Object> removedCdo, Optional<CommitMetadata> commitMetadata) {
        super(removed, removedCdo, commitMetadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ObjectRemoved) {
            ObjectRemoved that = (ObjectRemoved) obj;
            return super.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String prettyPrint(PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);
        return "object removed: " + getAffectedGlobalId().value();
    }
}
