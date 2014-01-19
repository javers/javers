package org.javers.core.diff.changetype.map;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * entry value changed
 *
 * @author bartosz walacik
 */
public class EntryValueChanged extends EntryChange {
    private final Object key;
    private final Object leftValue;
    private final Object rightValue;

    public EntryValueChanged(Object key, Object leftValue, Object rightValue) {
        argumentIsNotNull(key);

        this.key = key;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public Object getKey() {
        return key;
    }

    public Object getLeftValue() {
        return leftValue;
    }

    public Object getRightValue() {
        return rightValue;
    }
}
