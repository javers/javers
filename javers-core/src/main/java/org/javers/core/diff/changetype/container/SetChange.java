package org.javers.core.diff.changetype.container;

import org.javers.core.metamodel.object.GlobalId;

import java.util.List;
import java.util.Objects;

import static org.javers.common.validation.Validate.conditionFulfilled;

/**
 * @author pawel szymczyk
 */
public final class SetChange extends CollectionChange {

    public SetChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes) {
        super(affectedCdoId, propertyName, changes);
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
            return Objects.equals(this.getAffectedGlobalId(), that.getAffectedGlobalId())
                    && Objects.equals(this.getPropertyName(), that.getPropertyName())
                    && Objects.equals(this.getChanges(), that.getChanges());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAffectedGlobalId(), getPropertyName(), getChanges());
    }
}
