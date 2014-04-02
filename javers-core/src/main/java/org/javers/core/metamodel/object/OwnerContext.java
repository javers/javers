package org.javers.core.metamodel.object;

/**
* @author bartosz walacik
*/
public class OwnerContext {
    final GlobalCdoId owner;
    final String propertyName;
    private String fragment;

    public OwnerContext(GlobalCdoId owner, String propertyName) {
        this.owner = owner;
        this.propertyName = propertyName;
    }

    public GlobalCdoId getGlobalCdoId() {
        return owner;
    }

    public String getPath() {
        if (fragment == null){
            return propertyName;
        }

        return propertyName+"/"+fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

}
