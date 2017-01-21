package org.javers.core.diff.appenders;

import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ElementValueChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.diff.changetype.map.EntryAdded;
import org.javers.core.diff.changetype.map.EntryChange;
import org.javers.core.diff.changetype.map.EntryRemoved;
import org.javers.core.diff.changetype.map.EntryValueChange;

import java.util.function.Function;

/**
 * @author pawel szymczyk
 */
class MapChangesToListChangesFunction implements Function<EntryChange, ContainerElementChange> {

    @Override
    public ContainerElementChange apply(EntryChange input) {
        int index = (int)input.getKey();
        if (input instanceof EntryAdded) {
            return new ValueAdded(index, ((EntryAdded) input).getValue());
        } else if (input instanceof EntryRemoved) {
            return new ValueRemoved(index, ((EntryRemoved) input).getValue());
        } else if (input instanceof EntryValueChange) {
            return new ElementValueChange(index, ((EntryValueChange) input).getLeftValue(),
                                                 ((EntryValueChange) input).getRightValue());
        }

        throw new IllegalArgumentException("Unknown change type: " + input.getClass().getSimpleName());
    }
}
