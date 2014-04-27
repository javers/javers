package org.javers.core.diff.changetype.map;

import org.javers.core.diff.changetype.Value;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * entry value changed, when value is simple type
 *
 * @author bartosz walacik
 */
public class EntryValueChange extends EntryChange {
    private final Value leftValue;
    private final Value rightValue;

    public EntryValueChange(Object key, Object leftValue, Object rightValue) {
        super(key);
        this.leftValue = new Value(leftValue);
        this.rightValue = new Value(rightValue);
    }

    public Object getLeftValue() {
        return leftValue.unwrap();
    }

    public Object getRightValue() {
        return rightValue.unwrap();
    }

    public Value getWrappedLeftValue() {
        return leftValue;
    }

    public Value getWrappedRightValue() {
        return rightValue;
    }
}
