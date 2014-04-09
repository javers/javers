package org.javers.core.graph;

import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.GlobalCdoId;

/**
 * @author bartosz walacik
 */
public interface CdoFactory {
    Cdo create(Object target, GlobalCdoId globalId);
}
