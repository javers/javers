package org.javers.repository.sql.finders;

import org.javers.repository.sql.pico.TableNameManager;
import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_PK;

/**
 * @author bartosz.walacik
 */
class SnapshotIdFilter extends SnapshotFilter {

    private final long snapshotPK;

    SnapshotIdFilter(TableNameManager tableNameManager, long snapshotPK) {
        super(tableNameManager);
        this.snapshotPK = snapshotPK;
    }

    @Override
    void addWhere(SelectQuery query) {
        query.where(SNAPSHOT_PK + " = "+ snapshotPK);
    }
}
