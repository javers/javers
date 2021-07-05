package org.javers.core.diff.changetype.container;

import java.util.List;
import java.util.Objects;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.PropertyChangeMetadata;

/**
 * Changes on a List property
 *
 * @author pawel szymczyk
 */
public final class ListChange extends CollectionChange<List<Object>> {

    public ListChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes ,Atomic left, Atomic right) {
        super(metadata, changes,left,right);
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
