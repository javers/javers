package org.javers.core.diff.changetype;

import java.util.Optional;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;

/**
 * primitive or Value Object changed
 *
 * @author bartosz walacik
 */
public final class ObjectRemoved extends Change {
    public ObjectRemoved(GlobalId removed, Optional<Object> removedCdo) {
        super(removed, removedCdo);
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
}
