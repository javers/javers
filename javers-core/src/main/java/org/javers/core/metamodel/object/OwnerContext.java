package org.javers.core.metamodel.object;

/**
* @author bartosz walacik
*/
public class OwnerContext {
    final GlobalCdoId owner;
    final String path;

    public OwnerContext(GlobalCdoId owner, String path) {
        this.owner = owner;
        this.path = path;
    }

    public GlobalCdoId getGlobalCdoId() {
        return owner;
    }

    public String getPath() {
        return path;
    }
}
