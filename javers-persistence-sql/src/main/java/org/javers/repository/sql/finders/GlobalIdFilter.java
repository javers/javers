package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;

import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_GLOBAL_ID_FK;

/**
 * @author bartosz.walacik
 */
class GlobalIdFilter extends PrimaryKeySnapshotFilter {

    GlobalIdFilter(long globalIdPk, Optional<String> propertyName) {
        super(globalIdPk, SNAPSHOT_GLOBAL_ID_FK, propertyName);
    }

    GlobalIdFilter(List<Long> globalIdPks, Optional<String> propertyName) {
        super(globalIdPks, SNAPSHOT_GLOBAL_ID_FK, propertyName);
    }
}
