package org.javers.core.diff.changetype.container;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.javers.common.validation.Validate.conditionFulfilled;

/**
 * Changes on a Set property
 *
 * @author pawel szymczyk
 */
public final class SetChange extends CollectionChange {

    public SetChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes) {
        this(affectedCdoId, propertyName, changes, Optional.empty());
    }

    public SetChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes, Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, propertyName, changes, commitMetadata);
        for (ContainerElementChange change: changes){
            conditionFulfilled(change instanceof ValueAddOrRemove, "SetChange constructor failed, expected ValueAddOrRemove");
            conditionFulfilled(change.getIndex() == null, "SetChange constructor failed, expected empty change.index");
        }
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
