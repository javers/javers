package org.javers.core.diff.changetype.container;

import java.util.Objects;

import org.javers.common.string.ToStringBuilder;
import org.javers.core.diff.changetype.Atomic;

import static org.javers.common.string.ToStringBuilder.format;

/**
 * @author pawel szymczyk
 */
public class ElementValueChange extends ContainerElementChange {

    private final Atomic leftValue;
    private final Atomic rightValue;

    public ElementValueChange(int index, Object leftValue, Object rightValue) {
        super(index);
        this.leftValue = new Atomic(leftValue);
        this.rightValue = new Atomic(rightValue);
    }

    public Object getLeftValue() {
        return leftValue.unwrap();
    }

    public Object getRightValue() {
        return rightValue.unwrap();
    }

    @Override
    public String toString() {
        return getIndex() + ". "+ format(getLeftValue())+" changed to "+format(getRightValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ElementValueChange) {
            ElementValueChange that = (ElementValueChange) obj;
            return super.equals(that)
                    && Objects.equals(this.getLeftValue(), that.getLeftValue())
                    && Objects.equals(this.getRightValue(), that.getRightValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLeftValue(), getRightValue());
    }

}
