package org.javers.model.domain.changeType;

import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.PropertyChange;
import org.javers.model.mapping.Property;

/**
 * element added to collection of values
 *
 * @author bartosz walacik
 */
public class ValueAdded extends PropertyChange {
    private final Object value;

    public ValueAdded(GlobalCdoId globalCdoId, Property property, Object value) {
        super(globalCdoId, property);
        this.value = value;
    }

    public Object getAddedValue() {
        return value;
    }
}
