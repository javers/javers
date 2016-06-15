package org.javers.core.diff.changetype.container;

import org.javers.core.metamodel.object.GlobalId;

import java.util.List;
import java.util.Objects;

/**
 * @author pawel szymczyk
 */
public final class ArrayChange extends ContainerChange {

    public ArrayChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes) {
        super(affectedCdoId, propertyName, changes);
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
