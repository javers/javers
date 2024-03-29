package org.javers.core.diff.changetype.container;

import java.util.Collections;
import java.util.Set;
import org.javers.core.diff.changetype.PropertyChangeMetadata;

import java.util.List;
import java.util.Objects;

import static org.javers.common.validation.Validate.conditionFulfilled;

/**
 * Changes on a Set property
 *
 * @author pawel szymczyk
 */
public final class SetChange extends CollectionChange<Set<?>> {

    public SetChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, Set left, Set right) {
        super(metadata, changes, left, right);
        for (ContainerElementChange change: changes){
            conditionFulfilled(change instanceof ValueAddOrRemove, "SetChange constructor failed, expected ValueAddOrRemove");
            conditionFulfilled(change.getIndex() == null, "SetChange constructor failed, expected empty change.index");
        }
    }

    public SetChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes) {
        super(metadata, changes, Collections.emptySet(), Collections.emptySet());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SetChange) {
            SetChange that = (SetChange) obj;
            return super.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
