package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;

/**
 * reference removed from a collection (in *ToMany relation)
 *
 * @author bartosz walacik
 */
public class ReferenceRemoved extends ReferenceAddOrRemove {

    public ReferenceRemoved(int index, GlobalCdoId removed) {
        super(index, removed);
    }

    public ReferenceRemoved(GlobalCdoId removed) {
        super(removed);
    }

    public GlobalCdoId getRemovedReference() {
        return reference;
    }
}
