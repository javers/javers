package org.javers.core.diff.changetype;

import org.javers.common.collections.Optional;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Objects;

/**
 * new CDO added to graph
 *
 * @author bartosz walacik
 */
public final class NewObject extends Change {
    public NewObject(GlobalId newId, Optional<Object> newCdo) {
        super(newId);
        setAffectedCdo(newCdo);
    }

    public NewObject(GlobalId newId, Optional<Object> newCdo, CommitMetadata commitMetadata) {
        this(newId, newCdo);
        bindToCommit(commitMetadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NewObject) {
            NewObject that = (NewObject) obj;
            return Objects.equals(this.getAffectedGlobalId(), that.getAffectedGlobalId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAffectedGlobalId());
    }
}
