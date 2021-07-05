package org.javers.core.diff.changetype.container;

import java.util.Collection;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.PropertyChangeMetadata;

import java.util.List;
import java.util.Objects;

/**
 * Changes on an Array property
 *
 * @author pawel szymczyk
 */
public final class ArrayChange extends ContainerChange<Object[]> {


    @Override
    public Object[] getRight() {
        return ((Collection)super.getUnwrappedRight().unwrap()).toArray();
    }

    @Override
    public Object[] getLeft() {
        return ((Collection)super.getUnwrappedLeft().unwrap()).toArray();
    }

    public ArrayChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, Atomic left, Atomic right) {
        super(metadata, changes,left,right);
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
