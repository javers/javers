package org.javers.core.metamodel.object;

/**
* @author bartosz walacik
*/
public class OwnerContext {
    final GlobalCdoId owner;
    final String propertyName;
    private Integer listIndex;

    public OwnerContext(GlobalCdoId owner, String propertyName) {
        this.owner = owner;
        this.propertyName = propertyName;
    }

    public GlobalCdoId getGlobalCdoId() {
        return owner;
    }

    String getPath() {
        if (listIndex==null){
            return propertyName;
        } else {
            return propertyName+"/"+listIndex;
        }
    }

    public void incListIndex() {
        listIndex++;
    }

    public void startListIndex() {
        listIndex = 0;
    }

    public void setListIndex(Integer listIndex) {
        this.listIndex = listIndex;
    }
}
