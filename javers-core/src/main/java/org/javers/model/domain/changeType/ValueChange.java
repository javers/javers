package org.javers.model.domain.changeType;

import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.PropertyChange;
import org.javers.model.domain.Value;
import org.javers.model.mapping.Property;

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
        return leftValue.getValue();
    }

    public Object getRightValue() {
        return rightValue.getValue();
    }
}
