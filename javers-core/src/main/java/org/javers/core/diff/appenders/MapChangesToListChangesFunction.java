package org.javers.core.diff.appenders;

import org.javers.common.collections.Function;
import org.javers.core.diff.changetype.ContainerValueChange;
import org.javers.core.diff.changetype.ElementAdded;
import org.javers.core.diff.changetype.ElementRemoved;
import org.javers.core.diff.changetype.ElementValueChange;
import org.javers.core.diff.changetype.map.EntryAdded;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.diff.changetype.map.EntryRemoved;
import org.javers.core.diff.changetype.map.EntryValueChanged;

/**
 * @author pawel szymczyk
 */
public class MapChangesToListChangesFunction implements Function<EntryChange, ContainerValueChange> {

    @Override
    public ContainerValueChange apply(EntryChange input) {
        if (input instanceof EntryAdded) {
            return new ElementAdded(((EntryAdded) input).getValue());
        } else if (input instanceof EntryRemoved) {
            return new ElementRemoved(((EntryRemoved) input).getValue());
        } else if (input instanceof EntryValueChanged) {
            return new ElementValueChange(((EntryValueChanged) input).getLeftValue(),
                                          ((EntryValueChanged) input).getRightValue());
        }

        throw new IllegalArgumentException("Unknown change type: " + input.getClass().getSimpleName());
    }
}
