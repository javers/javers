package org.javers.core.diff.changetype.container;

import org.javers.core.metamodel.object.GlobalId;

import java.util.List;
import java.util.Objects;

/**
 * @author pawel szymczyk
 */
public final class ListChange extends CollectionChange {

    public ListChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes) {
        super(affectedCdoId, propertyName, changes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ListChange) {
            ListChange that = (ListChange) obj;
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
