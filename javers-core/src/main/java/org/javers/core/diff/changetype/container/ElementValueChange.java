package org.javers.core.diff.changetype.container;

import org.javers.core.diff.changetype.Atomic;

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
        return "("+ getIndex() + ").'"+getLeftValue()+"'>>'"+getRightValue()+"'";
    }

}
