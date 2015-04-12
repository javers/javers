package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.polyjdbc.core.query.SelectQuery;
import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_CHANGED;

/**
 * Created by bartosz.walacik on 2015-04-12.
 */
abstract class PropertyNameFilter extends SnapshotFilter {
    final Optional<String> propertyName;

    PropertyNameFilter(long primaryKey, String pkFieldName, Optional<String> propertyName) {
        super(primaryKey, pkFieldName);
        this.propertyName = propertyName;
    }

    @Override
    void addWhere(SelectQuery query) {
        if (propertyName.isPresent()) {
            query.where(pkFieldName + " = :pk " +
                    " AND " + SNAPSHOT_CHANGED + " like '%\"" + propertyName.get() + "\"%'")
                    .withArgument("pk", primaryKey);
        } else {
            super.addWhere(query);
        }
    }
}
