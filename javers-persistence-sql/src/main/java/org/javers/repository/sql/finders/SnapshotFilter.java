package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.polyjdbc.core.query.SelectQuery;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * yes, assembling SQL using SnapshotFilters classes is tricky,
 * any better ideas how to deal with it without code repetition?
 */
abstract class SnapshotFilter {
    static final String COMMIT_WITH_SNAPSHOT
            = SNAPSHOT_TABLE_NAME + " INNER JOIN " + COMMIT_TABLE_NAME + " ON " + COMMIT_PK + " = " + SNAPSHOT_COMMIT_FK;

    static final String BASE_FIELDS =
            SNAPSHOT_STATE + ", " +
            SNAPSHOT_TYPE + ", " +
            SNAPSHOT_CHANGED + ", " +
            COMMIT_AUTHOR + ", " +
            COMMIT_COMMIT_DATE + ", " +
            COMMIT_COMMIT_ID;

    final long primaryKey;
    final String pkFieldName;
    final Optional<String> propertyName;

    public SnapshotFilter(long primaryKey, String pkFieldName, Optional<String> propertyName) {
        this.primaryKey = primaryKey;
        this.pkFieldName = pkFieldName;
        this.propertyName = propertyName;
    }

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

    void addFrom(SelectQuery query) {
        query.from(COMMIT_WITH_SNAPSHOT);
    }

    String select(){
        return BASE_FIELDS;
    }
}
