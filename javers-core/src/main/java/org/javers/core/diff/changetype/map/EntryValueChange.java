package org.javers.core.diff.changetype.map;

import org.javers.core.diff.changetype.Atomic;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * entry value changed, when value is simple type
 *
 * @author bartosz walacik
 */
public class EntryValueChange extends EntryChange {
    private final Atomic leftValue;
    private final Atomic rightValue;

    public EntryValueChange(Object key, Object leftValue, Object rightValue) {
        super(key);
        this.leftValue = new Atomic(leftValue);
        this.rightValue = new Atomic(rightValue);
    }

    public Object getLeftValue() {
        return leftValue.unwrap();
    }

    public Object getRightValue() {
        return rightValue.unwrap();
    }

    public Atomic getWrappedLeftValue() {
        return leftValue;
    }

    public Atomic getWrappedRightValue() {
        return rightValue;
    }
}
