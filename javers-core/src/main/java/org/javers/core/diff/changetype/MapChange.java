package org.javers.core.diff.changetype;

import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Property;

/**
 * @author bartosz walacik
 */
public abstract class MapChange extends PropertyChange {

    protected MapChange(GlobalCdoId globalCdoId, Property property) {
        super(globalCdoId, property);
    }
}
