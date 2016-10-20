package org.javers.core.graph;

import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.OwnerContext;

/**
 * @author bartosz walacik
 */
public interface CdoFactory {
    Cdo create(Object target, OwnerContext owner);

    Cdo create(Object target, OwnerContext owner, boolean shallowReference);

    String typeDesc();
}
