package org.javers.core.diff.appenders;

import org.javers.common.collections.Function;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ValueAdded;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.ValueRemoved;
import org.javers.core.diff.changetype.map.EntryAdded;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.diff.changetype.map.EntryRemoved;
import org.javers.core.diff.changetype.map.EntryValueChanged;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

public class MapChangesToListChangesFunction implements Function<EntryChange,PropertyChange> {

    private GlobalCdoId id;
    private Property property;

    public MapChangesToListChangesFunction(GlobalCdoId id, Property property) {
        this.id = id;
        this.property = property;
    }

    @Override
    public PropertyChange apply(EntryChange input) {
        if (input instanceof EntryAdded) {
            return new ValueAdded(id, property, ((EntryAdded) input).getValue());
        } else if (input instanceof EntryRemoved) {
            return new ValueRemoved(id, property, ((EntryRemoved) input).getValue());
        } else if (input instanceof EntryValueChanged) {
            return new ValueChange(id, property,
                    ((EntryValueChanged) input).getLeftValue(),
                    ((EntryValueChanged) input).getRightValue());
        }

        throw new IllegalArgumentException("bla bla");
    }
}
