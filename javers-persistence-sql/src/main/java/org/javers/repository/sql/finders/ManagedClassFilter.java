package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_CHANGED;
import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_MANAGED_TYPE;

/**
 * @author bartosz.walacik
 */
class ManagedClassFilter extends SnapshotFilter {
    final String managedType;
    final Optional<String> propertyName;

    ManagedClassFilter(String managedType, Optional<String> propertyName) {
        this.managedType = managedType;
        this.propertyName = propertyName;
    }

    @Override
    String select() {
        return BASE_AND_GLOBAL_ID_FIELDS;
    }

    @Override
    void addFrom(SelectQuery query) {
        query.from(COMMIT_WITH_SNAPSHOT_GLOBAL_ID);
    }

    @Override
    void addWhere(SelectQuery query) {
        query.where(SNAPSHOT_MANAGED_TYPE + " = :managedType ").withArgument("managedType", managedType);
        if (propertyName.isPresent()) {
            query.append(" AND " + SNAPSHOT_CHANGED + " like '%\"" + propertyName.get() + "\"%'");
        }
    }
}
