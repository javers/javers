package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

/**
 * element removed from collection
 *
 * @author bartosz walacik
 */
public class ValueRemoved extends ValueAddOrRemove {

    public ValueRemoved(GlobalCdoId globalCdoId, Property property, Object value) {
        super(globalCdoId, property, value);
    }

    public Object getRemovedValue() {
        return value.unwrap();
    }
}
