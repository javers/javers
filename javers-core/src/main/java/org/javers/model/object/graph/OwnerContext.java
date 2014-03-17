package org.javers.model.object.graph;

import org.javers.core.metamodel.object.GlobalCdoId;

/**
* @author bartosz walacik
*/
class OwnerContext {
    private final ObjectWrapper owner;
    private final String propertyName;
    private Integer listIndex;

    OwnerContext(ObjectWrapper owner, String propertyName) {
        this.owner = owner;
        this.propertyName = propertyName;
    }

    GlobalCdoId getGlobalCdoId() {
        return owner.getGlobalCdoId();
    }

    String getPath() {
        if (listIndex==null){
            return propertyName;
        } else {
            return propertyName+"/"+listIndex;
        }
    }

    void incListIndex() {
        listIndex++;
    }

    void startListIndex() {
        listIndex = 0;
    }
}
