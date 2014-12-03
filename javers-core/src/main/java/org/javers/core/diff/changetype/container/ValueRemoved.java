package org.javers.core.diff.changetype.container;

import static org.javers.common.string.ToStringBuilder.toStringSimple;

/**
 * element removed from collection
 *
 * @author bartosz walacik
 */
public class ValueRemoved extends ValueAddOrRemove {

    public ValueRemoved(int index, Object value) {
        super(index, value);
    }

    public ValueRemoved(Object value) {
        super(value);
    }

    public Object getRemovedValue() {
        return value.unwrap();
    }

    @Override
    public String toString() {
        if (getIndex() == null){
            return toStringSimple("removed", getRemovedValue());
        }
        else{
            return toStringSimple("("+getIndex()+").removed", getRemovedValue());
        }
    }
}
