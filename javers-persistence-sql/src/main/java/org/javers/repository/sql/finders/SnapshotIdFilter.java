package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.metamodel.object.GlobalId;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_PK;

/**
 * @author bartosz.walacik
 */
class SnapshotIdFilter extends SnapshotFilter {
    final GlobalId globalId;

    SnapshotIdFilter(long snapshotId, GlobalId globalId) {
        super(snapshotId, SNAPSHOT_PK, Optional.<String>empty());
        this.globalId = globalId;
    }
}
