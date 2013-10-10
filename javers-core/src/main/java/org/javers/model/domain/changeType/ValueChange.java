package org.javers.model.domain.changeType;

import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.PropertyChange;
import org.javers.model.mapping.Property;

/**
 * @author bartosz walacik
 */
public class ValueChange extends PropertyChange {
    private final Object leftValue;
    private final Object rightValue;

    public ValueChange(GlobalCdoId globalCdoId, Property property, Object leftValue, Object rightValue) {
        super(globalCdoId, property);
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public Object getLeftValue() {
        return leftValue;
    }

    public Object getRightValue() {
        return rightValue;
    }
}
