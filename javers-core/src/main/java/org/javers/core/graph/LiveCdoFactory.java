package org.javers.core.graph;

import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.ManagedClass;

/**
 * @author bartosz walacik
 */
public class LiveCdoFactory implements CdoFactory {

    @Override
    public Cdo create (Object wrappedCdo, GlobalCdoId globalId){
        return new CdoWrapper(wrappedCdo, globalId);
    }
}
