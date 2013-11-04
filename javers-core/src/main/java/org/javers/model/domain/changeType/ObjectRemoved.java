package org.javers.model.domain.changeType;

import org.javers.model.domain.Change;
import org.javers.model.domain.GlobalCdoId;

/**
 * primitive or Value Object changed
 *
 * @author bartosz walacik
 */
public class ObjectRemoved extends Change {
    public ObjectRemoved(GlobalCdoId removed) {
        super(removed);
    }
}
