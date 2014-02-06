package org.javers.core.diff.changetype;

/**
 * element removed from collection
 *
 * @author bartosz walacik
 */
public class ElementRemoved extends ElementAddOrRemove {

    public ElementRemoved(Object value) {
        super(value);
    }

    public Object getRemovedValue() {
        return value.unwrap();
    }
}
