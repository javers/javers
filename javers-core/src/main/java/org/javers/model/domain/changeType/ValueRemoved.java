package org.javers.model.domain.changeType;

import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.PropertyChange;
import org.javers.model.mapping.Property;

/**
 * element removed from collection of values
 *
 * @author bartosz walacik
 */
public class ValueRemoved extends PropertyChange {
    private final Object value;

    public ValueRemoved(GlobalCdoId globalCdoId, Property property, Object value) {
        super(globalCdoId, property);
        this.value = value;
    }

    public Object getRemovedValue() {
        return value;
    }
}
