package org.javers.core.diff.changetype;

import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Property;

/**
 * Change on collection of values
 *
 * @author bartosz walacik
 */
public abstract class ValueAddOrRemove extends PropertyChange {
    protected final Value value;

    protected ValueAddOrRemove(GlobalCdoId globalCdoId, Property property, Object value) {
        super(globalCdoId, property);
        this.value = new Value(value);
    }
}
