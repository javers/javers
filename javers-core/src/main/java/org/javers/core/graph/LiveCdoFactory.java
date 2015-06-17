package org.javers.core.graph;

import org.javers.core.metamodel.object.*;

/**
 * @author bartosz walacik
 */
public class LiveCdoFactory implements CdoFactory {

    private final GlobalIdFactory globalIdFactory;
    private ObjectAccessHook objectAccessHook;

    public LiveCdoFactory(GlobalIdFactory globalIdFactory, ObjectAccessHook objectAccessHook) {
        this.globalIdFactory = globalIdFactory;
        this.objectAccessHook = objectAccessHook;
    }

    @Override
    public Cdo create (Object wrappedCdo, OwnerContext owner){
        wrappedCdo = objectAccessHook.access(wrappedCdo);
        GlobalId globalId = globalIdFactory.createId(wrappedCdo, owner);
        return new CdoWrapper(wrappedCdo, globalId);
    }

    @Override
    public String typeDesc() {
        return "live";
    }
}
