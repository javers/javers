package org.javers.core.diff.changetype.container;

import java.util.Collection;
import java.util.List;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.PropertyChangeMetadata;

/**
 * Changes on a Collection property
 *
 * @author bartosz walacik
 */
public abstract class CollectionChange<T extends Collection<?>> extends ContainerChange<T> {

    public CollectionChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, Collection left, Collection right) {
        super(metadata, changes, (T)left, (T)right);
    }
}
