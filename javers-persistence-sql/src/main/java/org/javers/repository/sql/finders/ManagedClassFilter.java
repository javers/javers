package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author bartosz.walacik
 */
class ManagedClassFilter extends PrimaryKeySnapshotFilter {
    ManagedClassFilter(long classPk, Optional<String> propertyName) {
        super(classPk, "g." + GLOBAL_ID_CLASS_FK, propertyName);
    }

    ManagedClassFilter(long classPk, String pkFieldName) {
        super(classPk, pkFieldName, Optional.<String>empty());
    }

    @Override
    String select() {
        return BASE_AND_GLOBAL_ID_FIELDS;
    }

    @Override
    void addFrom(SelectQuery query) {
        query.from(COMMIT_WITH_SNAPSHOT_GLOBAL_ID);
    }
}
