package org.javers.model.domain.changeType;

import org.javers.model.domain.Change;
import org.javers.model.domain.Diff;
import org.javers.model.domain.GlobalCdoId;

/**
 * new CDO added to graph
 *
 * @author bartosz walacik
 */
public class NewObject extends Change {
    public NewObject(GlobalCdoId added) {
        super(added);
    }
}
