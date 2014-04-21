package org.javers.core.diff.changetype;

/**
 * @author pawel szymczyk
 */
public class ElementValueChange extends ContainerValueChange{

    private final Value leftValue;
    private final Value rightValue;

    public ElementValueChange(Object leftValue, Object rightValue) {
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
