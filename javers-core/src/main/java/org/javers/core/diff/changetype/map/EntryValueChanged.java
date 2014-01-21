package org.javers.core.diff.changetype.map;

import org.javers.core.diff.changetype.Value;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * entry unwrap changed
 *
 * @author bartosz walacik
 */
public class EntryValueChanged extends EntryChange {
    private final Value key;
    private final Value leftValue;
    private final Value rightValue;

    public EntryValueChanged(Object key, Object leftValue, Object rightValue) {
        argumentIsNotNull(key);

        this.key = new Value(key);
        this.leftValue = new Value(leftValue);
        this.rightValue = new Value(rightValue);
    }

    public Object getKey() {
        return key.unwrap();
    }

    public Object getLeftValue() {
        return leftValue.unwrap();
    }

    public Object getRightValue() {
        return rightValue.unwrap();
    }

    public Value getWrappedKey() {
        return key;
    }

    public Value getWrappedLeftValue() {
        return leftValue;
    }

    public Value getWrappedRightValue() {
        return rightValue;
    }
}
