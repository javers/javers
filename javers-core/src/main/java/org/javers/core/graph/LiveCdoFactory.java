package org.javers.core.graph;

import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.ManagedClass;

/**
 * @author bartosz walacik
 */
public class LiveCdoFactory implements CdoFactory {

    private final GlobalIdFactory globalIdFactory;

    public LiveCdoFactory(GlobalIdFactory globalIdFactory) {
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public Cdo create (Object wrappedCdo, OwnerContext owner){
        GlobalCdoId globalId = globalIdFactory.createId(wrappedCdo, owner);
        return new CdoWrapper(wrappedCdo, globalId);
    }

    @Override
    public String typeDesc() {
        return "live";
    }
}
