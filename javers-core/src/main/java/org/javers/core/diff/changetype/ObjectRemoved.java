package org.javers.core.diff.changetype;

import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalCdoId;

/**
 * primitive or Value Object changed
 *
 * @author bartosz walacik
 */
public class ObjectRemoved extends Change {
    public ObjectRemoved(GlobalCdoId removed, Object removedCdo) {
        super(removed);
        setAffectedCdo(removedCdo);
    }
}
