package org.javers.model.domain.changeType;

import org.javers.model.domain.Diff;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.Value;
import org.javers.model.mapping.Property;

/**
 * element added to collection
 *
 * @author bartosz walacik
 */
public class ValueAdded extends ValueAddOrRemove {

    public ValueAdded(GlobalCdoId globalCdoId, Diff parent, Property property, Object value) {
        super(globalCdoId, parent, property, value);
    }

    public Value getAddedValue() {
        return value;
    }
}
