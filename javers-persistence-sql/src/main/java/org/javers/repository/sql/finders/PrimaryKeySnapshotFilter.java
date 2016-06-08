package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_CHANGED;

class PrimaryKeySnapshotFilter extends SnapshotFilter  {
    private final long primaryKey;
    private final String pkFieldName;
    private final Optional<String> propertyName;

    public PrimaryKeySnapshotFilter(long primaryKey, String pkFieldName, Optional<String> propertyName) {
        this.primaryKey = primaryKey;
        this.pkFieldName = pkFieldName;
        this.propertyName = propertyName;
    }

    @Override
    void addWhere(SelectQuery query) {
        if (propertyName.isPresent()) {
            query.where(pkFieldName + " = :pk " +
                        " AND " + SNAPSHOT_CHANGED + " like '%\"" + propertyName.get() + "\"%'")
                 .withArgument("pk", primaryKey);
        } else {
            query.where(pkFieldName + " = :pk")
                 .withArgument("pk", primaryKey);
        }
    }

}
