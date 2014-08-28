package org.javers.core.diff.changetype;

import org.javers.common.collections.Optional;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;

/**
 * primitive or Value Object changed
 *
 * @author bartosz walacik
 */
public class ObjectRemoved extends Change {
    public ObjectRemoved(GlobalId removed, Optional<Object> removedCdo) {
        super(removed);
        setAffectedCdo(removedCdo);
    }
}
