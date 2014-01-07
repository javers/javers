package org.javers.core.diff.changetype;

import org.javers.model.domain.GlobalCdoId;
import org.javers.core.diff.Value;
import org.javers.model.mapping.Property;

/**
 * element added to collection
 *
 * @author bartosz walacik
 */
public class ValueAdded extends ValueAddOrRemove {

    public ValueAdded(GlobalCdoId globalCdoId, Property property, Object value) {
        super(globalCdoId, property, value);
    }

    public Object getAddedValue() {
        return value;
    }
}
