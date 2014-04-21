package org.javers.core.diff.changetype;

/**
 * element added to collection
 *
 * @author bartosz walacik
 */
public class ElementAdded extends ElementAddOrRemove {

    public ElementAdded(int index, Object value) {
        super(index, value);
    }

    public ElementAdded(Object value) {
        super(value);
    }

    public Object getAddedValue() {
        return value.unwrap();
    }
}
