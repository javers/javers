package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_PK;

/**
 * @author bartosz.walacik
 */
class SnapshotIdFilter extends PrimaryKeySnapshotFilter {

    SnapshotIdFilter(long snapshotId) {
        super(snapshotId, SNAPSHOT_PK, Optional.<String>empty());
    }
}
