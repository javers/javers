package org.javers.core.metamodel.object;

/**
* @author bartosz walacik
*/
public abstract class OwnerContext {
    final   GlobalCdoId owner;
    final   String propertyName;

    OwnerContext(GlobalCdoId owner, String propertyName) {
        this.owner = owner;
        this.propertyName = propertyName;
    }

    public GlobalCdoId getGlobalCdoId() {
        return owner;
    }

    public String getPath() {
        return propertyName;
    }

    String getPropertyName() {
        return propertyName;
    }
}
