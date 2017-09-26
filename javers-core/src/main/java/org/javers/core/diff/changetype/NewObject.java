package org.javers.core.diff.changetype;

import java.util.Optional;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;

/**
 * new CDO added to graph
 *
 * @author bartosz walacik
 */
public final class NewObject extends Change {
    public NewObject(GlobalId newId, Optional<Object> newCdo) {
        super(newId, newCdo);
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
    public String toString() {
        return "new object: " + getAffectedGlobalId();
    }
}
