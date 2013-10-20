package org.javers.model.domain.changeType;

import org.javers.model.domain.Diff;
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

    public ValueChange(GlobalCdoId globalCdoId, Diff parent, Property property, Object leftValue, Object rightValue) {
        super(globalCdoId, parent, property);
        this.leftValue = new Value(leftValue);
        this.rightValue = new Value(rightValue);
    }

    /**
     * never returns null
     */
    public Value getLeftValue() {
        return leftValue;
    }

    /**
     * never returns null
     */
    public Value getRightValue() {
        return rightValue;
    }

    public void dehydrate(String leftValueJSON, String rightValueJson) {
        leftValue.dehydrate(leftValueJSON);
        rightValue.dehydrate(rightValueJson);
    }
}
