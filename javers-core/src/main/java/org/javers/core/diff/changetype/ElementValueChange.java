package org.javers.core.diff.changetype;

/**
 * @author pawel szymczyk
 */
public class ElementValueChange extends ContainerElementChange {

    private final Value leftValue;
    private final Value rightValue;

    public ElementValueChange(int index, Object leftValue, Object rightValue) {
        super(index);
        this.leftValue = new Value(leftValue);
        this.rightValue = new Value(rightValue);
    }

    public Object getLeftValue() {
        return leftValue.unwrap();
    }

    public Object getRightValue() {
        return rightValue.unwrap();
    }

}
