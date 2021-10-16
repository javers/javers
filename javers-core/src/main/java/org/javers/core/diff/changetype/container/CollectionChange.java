package org.javers.core.diff.changetype.container;

import org.javers.common.collections.Collections;
import org.javers.core.diff.changetype.PropertyChangeMetadata;

import java.util.Collection;
import java.util.List;

/**
 * Changes on a Collection property
 *
 * @author bartosz walacik
 */
public abstract class CollectionChange<T extends Collection<?>> extends ContainerChange<T> {

    public CollectionChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, Collection left, Collection right) {
        super(metadata, changes, (T)left, (T)right);
    }

    /**
     * size of right (or old) Collection at {@link #getRight()}
     */
    @Override
    public int getRightSize() {
        return Collections.size(getRight());
    }

    /**
     * size of left (or old) Collection at {@link #getLeft()}
     */
    @Override
    public int getLeftSize() {
        return Collections.size(getLeft());
    }
}
