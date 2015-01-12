package org.javers.core.diff.changetype;

import org.javers.common.collections.Optional;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;

/**
 * new CDO added to graph
 *
 * @author bartosz walacik
 */
public class NewObject extends Change {
    public NewObject(GlobalId newId, Optional<Object> newCdo) {
        super(newId);
        setAffectedCdo(newCdo);
    }

    public NewObject(GlobalId newId, Optional<Object> newCdo, CommitMetadata commitMetadata) {
        this(newId, newCdo);
        bindToCommit(commitMetadata);
    }
}
