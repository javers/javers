package org.javers.core.diff.changetype.container;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.PropertyChangeMetadata;

/**
 * Changes on a List property
 *
 * @author pawel szymczyk
 */
public final class ListChange extends CollectionChange<List<?>> {

    public ListChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, List left, List right) {
        super(metadata, changes,left,right);
    }

    public ListChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        super(metadata, changes, Collections.emptyList(), Collections.emptyList());
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
