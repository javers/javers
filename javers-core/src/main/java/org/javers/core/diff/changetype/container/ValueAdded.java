package org.javers.core.diff.changetype.container;

import static org.javers.common.string.ToStringBuilder.toStringSimple;

/**
 * element added to collection
 *
 * @author bartosz walacik
 */
public class ValueAdded extends ValueAddOrRemove {

    public ValueAdded(int index, Object value) {
        super(index, value);
    }

    public ValueAdded(Object value) {
        super(value);
    }

    public Object getAddedValue() {
        return value.unwrap();
    }

    @Override
    public String toString() {
        if (getIndex() == null){
            return toStringSimple("added", getAddedValue());
        }
        else{
            return toStringSimple("("+getIndex()+").added", getAddedValue());
        }
    }
}
