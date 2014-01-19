package org.javers.core.diff.changetype;

import org.javers.common.validation.Validate;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Property;

import java.util.Map;

/**
 * @author bartosz walacik
 */
public class EntryRemoved extends MapChange {
    private final Entry entry;

    public EntryRemoved(GlobalCdoId globalCdoId, Property property, Map.Entry removed) {
        super(globalCdoId, property);
        Validate.argumentsAreNotNull(globalCdoId, property, removed);

        this.entry = new Entry(removed);
    }

    public Entry getEntry() {
        return entry;
    }
}
