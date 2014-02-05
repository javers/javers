package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

/**
 * @author bartosz walacik
 */
public class ValueChange extends PropertyChange {
    private final Value leftValue;
    private final Value rightValue;

    public ValueChange(GlobalCdoId globalCdoId, Property property, Object leftValue, Object rightValue) {
        super(globalCdoId, property);
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
