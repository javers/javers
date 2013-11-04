package org.javers.model.domain.changeType;

import org.javers.model.domain.Diff;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.Value;
import org.javers.model.mapping.Property;

/**
 * element removed from collection
 *
 * @author bartosz walacik
 */
public class ValueRemoved extends ValueAddOrRemove {

    public ValueRemoved(GlobalCdoId globalCdoId, Diff parent, Property property, Object value) {
        super(globalCdoId, parent, property, value);
    }

    public Value getRemovedValue() {
        return value;
    }
}
