package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;

/**
 * @author bartosz walacik
 */
public class ReferenceAddOrRemove extends ContainerElementChange {
    final GlobalCdoId reference;

    ReferenceAddOrRemove(GlobalCdoId reference) {
        this.reference = reference;
    }

    ReferenceAddOrRemove(Integer index, GlobalCdoId reference) {
        super(index);
        this.reference = reference;
    }


}
