package org.javers.core.diff.changetype.container;

import org.javers.core.diff.changetype.PropertyChangeMetadata;

import java.util.List;

/**
 * Changes on a Collection property
 *
 * @author bartosz walacik
 */
public abstract class CollectionChange extends ContainerChange {
    public CollectionChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        super(metadata, changes);
    }
}
