package org.javers.core.diff.changetype.container;

import org.javers.core.diff.changetype.PropertyChangeMetadata;

import java.util.List;
import java.util.Objects;

/**
 * Changes on a List property
 *
 * @author pawel szymczyk
 */
public final class ListChange extends CollectionChange {

    public ListChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        super(metadata, changes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ListChange) {
            ListChange that = (ListChange) obj;
            return super.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
