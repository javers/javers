package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_GLOBAL_ID_FK;

/**
 * @author bartosz.walacik
 */
class GlobalIdFilter extends PrimaryKeySnapshotFilter {

    GlobalIdFilter(long globalIdPk, Optional<String> propertyName) {
        super(globalIdPk, SNAPSHOT_GLOBAL_ID_FK, propertyName);
    }
}
