package org.javers.core.diff.changetype.container;

import org.javers.common.string.ToStringBuilder;

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

    /**
     * Removed item. See {@link #getValue()} javadoc
     */
    public Object getRemovedValue() {
        return value.unwrap();
    }

    @Override
    public String toString() {
        return (getIndex() == null ? "" : getIndex()) + ". " +
                ToStringBuilder.format(getRemovedValue()) + " removed";
    }
}
