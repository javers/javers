package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.commit.CommitId;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.type.Timestamp;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

class PrimaryKeySnapshotFilter extends SnapshotFilter  {
    final long primaryKey;
    final String pkFieldName;
    final Optional<String> propertyName;

    public PrimaryKeySnapshotFilter(long primaryKey, String pkFieldName, Optional<String> propertyName) {
        this.primaryKey = primaryKey;
        this.pkFieldName = pkFieldName;
        this.propertyName = propertyName;
    }

    @Override
    String select() {
        return BASE_FIELDS;
    }

    @Override
    void addFrom(SelectQuery query) {
        query.from(COMMIT_WITH_SNAPSHOT);
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
