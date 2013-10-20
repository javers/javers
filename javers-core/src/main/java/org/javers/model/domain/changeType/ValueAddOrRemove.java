package org.javers.model.domain.changeType;

import org.javers.model.domain.Diff;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.PropertyChange;
import org.javers.model.domain.Value;
import org.javers.model.mapping.Property;

/**
 * Change on collection of values
 *
 * @author bartosz walacik
 */
public abstract class ValueAddOrRemove extends PropertyChange {
    protected final Value value;

    protected ValueAddOrRemove(GlobalCdoId globalCdoId, Diff parent, Property property, Object value) {
        super(globalCdoId, parent, property);
        this.value = new Value(value);
    }

    public void dehydrate(String valueJSON) {
        value.dehydrate(valueJSON);
    }
}
