package org.javers.core.diff.changetype.container;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Changes on a List property
 *
 * @author pawel szymczyk
 */
public final class ListChange extends CollectionChange {

    public ListChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes) {
        super(affectedCdoId, propertyName, changes, Optional.empty());
    }

    public ListChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes, Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, propertyName, changes, commitMetadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ListChange) {
            ListChange that = (ListChange) obj;
            return super.equals(that);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
