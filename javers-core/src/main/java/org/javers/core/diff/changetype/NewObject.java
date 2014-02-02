package org.javers.core.diff.changetype;

import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalCdoId;

/**
 * new CDO added to graph
 *
 * @author bartosz walacik
 */
public class NewObject extends Change {
    public NewObject(GlobalCdoId newId, Object newCdo) {
        super(newId);
        setAffectedCdo(newCdo);
    }
}
