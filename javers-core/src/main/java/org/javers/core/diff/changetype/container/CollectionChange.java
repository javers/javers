package org.javers.core.diff.changetype.container;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

import java.util.List;
import java.util.Optional;

/**
 * Changes on a Collection property
 *
 * @author bartosz walacik
 */
public abstract class CollectionChange extends ContainerChange {
    public CollectionChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes, Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, propertyName, changes, commitMetadata);
    }
}
