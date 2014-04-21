package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;

/**
 * reference added to a collection (in *ToMany relation)
 *
 * @author bartosz walacik
 */
public class ReferenceAdded extends ReferenceAddOrRemove {

    public ReferenceAdded(int index, GlobalCdoId added) {
        super(index, added);
    }

    public ReferenceAdded(GlobalCdoId added) {
        super(added);
    }

    public GlobalCdoId getAddedReference() {
        return reference;
    }
}
