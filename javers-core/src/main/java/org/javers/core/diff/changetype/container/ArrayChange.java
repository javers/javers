package org.javers.core.diff.changetype.container;

import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.PropertyChangeMetadata;

import java.util.List;
import java.util.Objects;

/**
 * Changes on an Array property
 *
 * @author pawel szymczyk
 */
public final class ArrayChange extends ContainerChange<Object> {

    public ArrayChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, Object left, Object right) {
        super(metadata, changes, left, right);
    }

    public ArrayChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        super(metadata, changes, null, null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ArrayChange) {
            ArrayChange that = (ArrayChange) obj;
            return super.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
