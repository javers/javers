package org.javers.core.snapshot;

import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;

/**
 * @author bartosz walacik
 */
public class SnapshotFactory {

    public CdoSnapshot create (Object liveCdo, GlobalCdoId id) {
        return new CdoSnapshot(id);
    }
}
