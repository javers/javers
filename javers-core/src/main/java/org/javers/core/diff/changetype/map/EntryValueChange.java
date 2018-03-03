package org.javers.core.diff.changetype.map;

import java.util.Objects;

import org.javers.core.diff.changetype.Atomic;

import static org.javers.common.string.ToStringBuilder.format;

/**
 * entry value changed, when value is a simple type
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

    @Override
    public String toString() {
        return format(getKey()) + " : " + format(getLeftValue())+" changed to "+format(getRightValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof EntryValueChange) {
            EntryValueChange that = (EntryValueChange) obj;
            return super.equals(that)
                    && Objects.equals(this.leftValue, that.leftValue)
                    && Objects.equals(this.rightValue, that.rightValue);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), leftValue, rightValue);
    }
}
