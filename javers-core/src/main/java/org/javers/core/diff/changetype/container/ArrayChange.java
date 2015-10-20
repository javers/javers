package org.javers.core.diff.changetype.container;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public final class ArrayChange extends ContainerChange {

    public ArrayChange(GlobalId affectedCdoId, Property property, List<ContainerElementChange> changes) {
        super(affectedCdoId, property, changes);
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
