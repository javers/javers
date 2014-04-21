package org.javers.core.diff.appenders;

import org.javers.common.collections.Function;
import org.javers.core.diff.changetype.ContainerElementChange;
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
public class MapChangesToListChangesFunction implements Function<EntryChange, ContainerElementChange> {

    @Override
    public ContainerElementChange apply(EntryChange input) {
        int index = (int)input.getKey();
        if (input instanceof EntryAdded) {
            return new ElementAdded(index, ((EntryAdded) input).getValue());
        } else if (input instanceof EntryRemoved) {
            return new ElementRemoved(index, ((EntryRemoved) input).getValue());
        } else if (input instanceof EntryValueChanged) {
            return new ElementValueChange(index, ((EntryValueChanged) input).getLeftValue(),
                                                 ((EntryValueChanged) input).getRightValue());
        }

        throw new IllegalArgumentException("Unknown change type: " + input.getClass().getSimpleName());
    }
}
