package org.javers.core.diff.changetype;

import org.javers.common.collections.Optional;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Objects;

/**
 * primitive or Value Object changed
 *
 * @author bartosz walacik
 */
public final class ObjectRemoved extends Change {
    public ObjectRemoved(GlobalId removed, Optional<Object> removedCdo) {
        super(removed);
        setAffectedCdo(removedCdo);
    }

    public ObjectRemoved(GlobalId removed, Optional<Object> removedCdo, CommitMetadata commitMetadata) {
        this(removed, removedCdo);
        bindToCommit(commitMetadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ObjectRemoved) {
            ObjectRemoved that = (ObjectRemoved) obj;
            return Objects.equals(this.getAffectedGlobalId(), that.getAffectedGlobalId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAffectedGlobalId());
    }
}
