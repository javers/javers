package org.javers.core.graph;

import org.javers.core.metamodel.object.*;

/**
 * @author bartosz walacik
 */
public class LiveCdoFactory implements CdoFactory {

    private final GlobalIdFactory globalIdFactory;
    private GraphFactoryHook graphFactoryHook;

    public LiveCdoFactory(GlobalIdFactory globalIdFactory, GraphFactoryHook graphFactoryHook) {
        this.globalIdFactory = globalIdFactory;
        this.graphFactoryHook = graphFactoryHook;
    }

    @Override
    public Cdo create (Object wrappedCdo, OwnerContext owner){
        wrappedCdo = graphFactoryHook.beforeAdd(wrappedCdo);
        GlobalId globalId = globalIdFactory.createId(wrappedCdo, owner);
        return new CdoWrapper(wrappedCdo, globalId);
    }

    @Override
    public String typeDesc() {
        return "live";
    }
}
