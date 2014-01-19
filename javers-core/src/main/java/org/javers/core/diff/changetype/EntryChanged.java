package org.javers.core.diff.changetype;

import org.javers.common.validation.Validate;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Property;

/**
 * entry value changed
 *
 * @author bartosz walacik
 */
public class EntryChanged extends MapChange {
    private final Object key;

    private final Object leftValue;
    private final Object rightValue;
    public EntryChanged(GlobalCdoId globalCdoId, Property property, Object key, Object leftValue, Object rightValue) {
        super(globalCdoId, property);
        Validate.argumentsAreNotNull(globalCdoId,property,key);

        this.key = key;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public Object getKey() {
        return key;
    }

    public Object getLeftValue() {
        return leftValue;
    }

    public Object getRightValue() {
        return rightValue;
    }
}
