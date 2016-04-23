package org.javers.repository.sql.finders;

import org.javers.core.commit.CommitId;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.type.Timestamp;

import java.util.Map;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

abstract class SnapshotFilter {
    static final String COMMIT_WITH_SNAPSHOT
        = SNAPSHOT_TABLE_NAME + " INNER JOIN " + COMMIT_TABLE_NAME + " ON " + COMMIT_PK + " = " + SNAPSHOT_COMMIT_FK;

    static final String COMMIT_WITH_SNAPSHOT_GLOBAL_ID =
        COMMIT_WITH_SNAPSHOT +
            " INNER JOIN " + GLOBAL_ID_TABLE_NAME + " g ON g." + GLOBAL_ID_PK + " = " + SNAPSHOT_GLOBAL_ID_FK +
            " LEFT OUTER JOIN " + GLOBAL_ID_TABLE_NAME + " o ON o." + GLOBAL_ID_PK + " = g." + GLOBAL_ID_OWNER_ID_FK;

    static final String BASE_FIELDS =
        SNAPSHOT_STATE + ", " +
            SNAPSHOT_TYPE + ", " +
            SNAPSHOT_VERSION + ", " +
            SNAPSHOT_CHANGED + ", " +
            SNAPSHOT_MANAGED_TYPE + ", " +
            COMMIT_PK + ", " +
            COMMIT_AUTHOR + ", " +
            COMMIT_COMMIT_DATE + ", " +
            COMMIT_COMMIT_ID;

    static final String BASE_AND_GLOBAL_ID_FIELDS =
        BASE_FIELDS + ", " +
            "g." + GLOBAL_ID_LOCAL_ID + ", " +
            "g." + GLOBAL_ID_FRAGMENT + ", " +
            "g." + GLOBAL_ID_OWNER_ID_FK + ", " +
            "o." + GLOBAL_ID_LOCAL_ID + " owner_" + GLOBAL_ID_LOCAL_ID + ", " +
            "o." + GLOBAL_ID_FRAGMENT + " owner_" + GLOBAL_ID_FRAGMENT + ", " +
            "o." + GLOBAL_ID_TYPE_NAME + " owner_" + GLOBAL_ID_TYPE_NAME;

    abstract String select();

    abstract void addFrom(SelectQuery query);

    abstract  void addWhere(SelectQuery query);

    void addFromDateCondition(SelectQuery query, LocalDateTime from) {
        query.append(" AND " + COMMIT_COMMIT_DATE + " >= :commitFromDate")
            .withArgument("commitFromDate", new Timestamp(from.toDate()));
    }

    void addToDateCondition(SelectQuery query, LocalDateTime to) {
        query.append(" AND " + COMMIT_COMMIT_DATE + " <= :commitToDate")
            .withArgument("commitToDate", new Timestamp(to.toDate()));
    }

    void addCommitIdCondition(SelectQuery query, CommitId commitId) {
        query.append(" AND " + COMMIT_COMMIT_ID + " = :commitId")
            .withArgument("commitId", commitId.valueAsNumber());
    }

    void addVersionCondition(SelectQuery query, Long version) {
        query.append(" AND " + SNAPSHOT_VERSION + " = :version")
            .withArgument("version", version);
    }

    void addCommitPropertyCondition(SelectQuery query, String propertyName, String propertyValue) {
        query.append(" AND EXISTS (" +
            "SELECT * FROM " + COMMIT_PROPERTY_TABLE_NAME +
            " WHERE " + COMMIT_PROPERTY_COMMIT_FK + " = " + COMMIT_PK +
            " AND " + COMMIT_PROPERTY_NAME + " = :propertyName_" + propertyName +
            " AND " + COMMIT_PROPERTY_VALUE + " = :propertyValue_" + propertyName +
            ")")
            .withArgument("propertyName_" + propertyName, propertyName)
            .withArgument("propertyValue_" + propertyName, propertyValue);
    }

}
