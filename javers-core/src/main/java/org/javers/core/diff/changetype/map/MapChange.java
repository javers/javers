package org.javers.core.diff.changetype.map;

import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class MapChange extends PropertyChange {
    private final List<EntryChange> changes;

    public MapChange(GlobalCdoId globalCdoId, Property property, List<EntryChange> changes) {
        super(globalCdoId, property);
        this.changes = new ArrayList<>(changes);
    }

    /**
     * @return unmodifiable list
     */
    public List<EntryChange> getEntryChanges() {
        return Collections.unmodifiableList(changes);
    }
}
