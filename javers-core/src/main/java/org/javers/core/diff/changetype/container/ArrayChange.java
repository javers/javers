package org.javers.core.diff.changetype.container;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author pawel szymczyk
 */
public final class ArrayChange extends ContainerChange {

    public ArrayChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes) {
        super(affectedCdoId, propertyName, changes, Optional.empty());

    }

    public ArrayChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes, Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, propertyName, changes, commitMetadata);
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

    public Object[] getLeftArray() {
        ArrayList<Object> left = new ArrayList<>();
        for (ContainerElementChange elementChange : this.getChanges()) {
            if (elementChange instanceof ElementValueChange) {
                left.add(((ElementValueChange) elementChange).getLeftValue());
            } else if (elementChange instanceof ValueRemoved) {
                left.add(((ValueRemoved) elementChange).getValue());
            }
        }
        return left.toArray();
    }

    public Object[] getRightArray() {
        ArrayList<Object> right = new ArrayList<>();
        for (ContainerElementChange elementChange : this.getChanges()) {
            if (elementChange instanceof ElementValueChange) {
                right.add(((ElementValueChange) elementChange).getRightValue());
            } else if (elementChange instanceof ValueAdded) {
                right.add(((ValueAdded) elementChange).getAddedValue());
            }
        }
        return right.toArray();
    }
}
