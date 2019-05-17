package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Optional;

/**
 * New object added to a graph
 *
 * @author bartosz walacik
 */
public final class NewObject extends Change {

    NewObject(GlobalId newId, Optional<Object> newCdo) {
        this(newId, newCdo, Optional.empty());
    }

    public NewObject(GlobalId newId, Optional<Object> newCdo, Optional<CommitMetadata> commitMetadata) {
        super(newId, newCdo, commitMetadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NewObject) {
            NewObject that = (NewObject) obj;
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
        return "new object: " + getAffectedGlobalId().value();
    }
}
