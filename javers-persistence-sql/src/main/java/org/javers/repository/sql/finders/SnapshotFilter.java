package org.javers.repository.sql.finders;

import org.javers.common.string.ToStringBuilder;
import org.javers.core.commit.CommitId;
import org.javers.repository.api.QueryParams;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.schema.TableNameProvider;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.type.Timestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters.toUtilDate;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

abstract class SnapshotFilter extends SchemaNameAware {

    SnapshotFilter(TableNameProvider tableNameProvider) {
        super(tableNameProvider);
    }

    private static final String BASE_FIELDS =
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

    protected String getFromCommitWithSnapshot() {
        return getSnapshotTableNameWithSchema() +
            " INNER JOIN " + getCommitTableNameWithSchema() + " ON " + COMMIT_PK + " = " + SNAPSHOT_COMMIT_FK +
            " INNER JOIN " + getGlobalIdTableNameWithSchema() + " g ON g." + GLOBAL_ID_PK + " = " + SNAPSHOT_GLOBAL_ID_FK +
            " LEFT OUTER JOIN " + getGlobalIdTableNameWithSchema() + " o ON o." + GLOBAL_ID_PK + " = g." + GLOBAL_ID_OWNER_ID_FK;
    }

    String select() {
        return BASE_AND_GLOBAL_ID_FIELDS;
    }

    void addFrom(SelectQuery query) {
        query.from(getFromCommitWithSnapshot());
    }

    abstract  void addWhere(SelectQuery query);

    void applyQueryParams(SelectQuery query, QueryParams queryParams) {
        if (queryParams.changedProperty().isPresent()){
            query.append(" AND " + SNAPSHOT_CHANGED + " like :changedProperty ")
                  .withArgument("changedProperty", "%\"" + queryParams.changedProperty().get() +"\"%");
        }
        if (queryParams.from().isPresent()) {
            query.append(" AND " + COMMIT_COMMIT_DATE + " >= :commitFromDate")
                 .withArgument("commitFromDate", new Timestamp(toUtilDate( queryParams.from().get())));
        }
        if (queryParams.to().isPresent()) {
            query.append(" AND " + COMMIT_COMMIT_DATE + " <= :commitToDate")
                 .withArgument("commitToDate", new Timestamp(toUtilDate(queryParams.to().get())));
        }
        if (queryParams.toCommitId().isPresent()) {
            query.append(" AND " + COMMIT_COMMIT_ID + " <= " + queryParams.toCommitId().get().valueAsNumber());
        }
        if (queryParams.commitIds().size() > 0) {
            query.append(" AND " + COMMIT_COMMIT_ID + " IN (" + ToStringBuilder.join(
                    queryParams.commitIds().stream().map(c -> c.valueAsNumber()).collect(Collectors.toList())) + ")");
        }
        if (queryParams.version().isPresent()) {
            query.append(" AND " + SNAPSHOT_VERSION + " = :version")
                 .withArgument("version", queryParams.version().get());
        }
        if (queryParams.author().isPresent()) {
            query.append(" AND " + COMMIT_AUTHOR + " = :author")
                 .withArgument("author",  queryParams.author().get());
        }
        if (queryParams.commitProperties().size() > 0) {
            addCommitPropertyConditions(query, queryParams.commitProperties());
        }
        if (queryParams.snapshotType().isPresent()){
            query.append(" AND " + SNAPSHOT_TYPE + " = :snapshotType")
                 .withArgument("snapshotType", queryParams.snapshotType().get().name());
        }
        query.limit(queryParams.limit(), queryParams.skip());
    }

    private void addCommitPropertyConditions(SelectQuery query, Map<String, String> commitProperties) {
        for (Map.Entry<String, String> commitProperty : commitProperties.entrySet()) {
            addCommitPropertyCondition(query, commitProperty.getKey(), commitProperty.getValue());
        }
    }

    private void addCommitPropertyCondition(SelectQuery query, String propertyName, String propertyValue) {
        query.append(" AND EXISTS (" +
            "SELECT * FROM " + getCommitPropertyTableNameWithSchema() +
            " WHERE " + COMMIT_PROPERTY_COMMIT_FK + " = " + COMMIT_PK +
            " AND " + COMMIT_PROPERTY_NAME + " = :propertyName_" + propertyName +
            " AND " + COMMIT_PROPERTY_VALUE + " = :propertyValue_" + propertyName +
            ")")
            .withArgument("propertyName_" + propertyName, propertyName)
            .withArgument("propertyValue_" + propertyName, propertyValue);
    }
}
