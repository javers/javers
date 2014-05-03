package org.javers.core.diff.changetype;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

/**
 * @author bartosz walacik
 */
public class ValueChange extends PropertyChange {
    private final Atomic leftValue;
    private final Atomic rightValue;

    public ValueChange(GlobalCdoId affectedCdoId, Property property, Object leftValue, Object rightValue) {
        super(affectedCdoId, property);
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
}
