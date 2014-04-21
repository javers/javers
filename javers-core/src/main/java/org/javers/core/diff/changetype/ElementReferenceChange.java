package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;

/**
 * Changed reference to Entity or VO, in List or Array
 *
 * @author bartosz walacik
 */
public class ElementReferenceChange extends ContainerElementChange{
    private final GlobalCdoId leftReference;
    private final GlobalCdoId rightReference;

    public ElementReferenceChange(GlobalCdoId leftReference, GlobalCdoId rightReference) {
        this.leftReference = leftReference;
        this.rightReference = rightReference;
    }

    public GlobalCdoId getLeftReference() {
        return leftReference;
    }

    public GlobalCdoId getRightReference() {
        return rightReference;
    }
}
