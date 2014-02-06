package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class ArrayChange extends PropertyChange {

    private final List<ContainerValueChange> changes;

    public ArrayChange(GlobalCdoId globalCdoId, Property property, List<ContainerValueChange> changes) {
        super(globalCdoId, property);
        this.changes = new ArrayList<>(changes);
    }

    /**
     * @return unmodifiable list
     */
    public List<ContainerValueChange> getChanges() {
        return Collections.unmodifiableList(changes);
    }
}
