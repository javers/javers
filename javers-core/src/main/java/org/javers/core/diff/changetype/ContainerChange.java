package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bartosz walacik
 */
public abstract class ContainerChange extends PropertyChange {
    private final List<ContainerValueChange> changes;

    ContainerChange(GlobalCdoId affectedCdoId, Property property, List<ContainerValueChange> changes) {
        super(affectedCdoId, property);
        this.changes= Collections.unmodifiableList(new ArrayList<>(changes));
    }

    /**
     * @return unmodifiable list
     */
    public List<ContainerValueChange> getChanges() {
        return changes;
    }
}
