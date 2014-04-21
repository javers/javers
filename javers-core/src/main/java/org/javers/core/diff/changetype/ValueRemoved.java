package org.javers.core.diff.changetype;

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
}
