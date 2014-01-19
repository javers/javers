package org.javers.core.diff.changetype;

import org.javers.common.validation.Validate;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Property;

import java.util.Map;

/**
 * @author bartosz walacik
 */
public class EntryAdded extends MapChange {
    private final Entry added;

    public EntryAdded(GlobalCdoId globalCdoId, Property property, Map.Entry added) {
        super(globalCdoId, property);
        Validate.argumentsAreNotNull(globalCdoId, property, added);

        this.added = new Entry(added);
    }

    public Entry getAdded() {

        return added;
    }
}
