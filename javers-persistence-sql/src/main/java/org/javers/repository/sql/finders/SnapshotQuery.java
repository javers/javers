package org.javers.repository.sql.finders;

import org.javers.common.string.ToStringBuilder;
import org.javers.core.json.CdoSnapshotSerialized;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.SnapshotIdentifier;
import org.javers.repository.sql.schema.TableNameProvider;
import org.javers.repository.sql.session.ObjectMapper;
import org.javers.repository.sql.session.Parameter;
import org.javers.repository.sql.session.SelectBuilder;
import org.javers.repository.sql.session.Session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;
import static org.javers.repository.sql.session.Parameter.*;

class SnapshotQuery {
    private final QueryParams queryParams;
    private final SelectBuilder selectBuilder;
    private final TableNameProvider tableNameProvider;
    private final CdoSnapshotMapper cdoSnapshotMapper = new CdoSnapshotMapper();

    public SnapshotQuery(TableNameProvider tableNames, QueryParams queryParams, Session session) {
        this.selectBuilder = session
            .select(
                SNAPSHOT_STATE + ", " +
                SNAPSHOT_TYPE + ", " +
                SNAPSHOT_VERSION + ", " +
                SNAPSHOT_CHANGED + ", " +
                SNAPSHOT_MANAGED_TYPE + ", " +
                COMMIT_PK + ", " +
                COMMIT_AUTHOR + ", " +
                COMMIT_COMMIT_DATE + ", " +
                COMMIT_COMMIT_DATE_INSTANT + ", " +
                COMMIT_COMMIT_ID + ", " +
                "g." + GLOBAL_ID_LOCAL_ID + ", " +
                "g." + GLOBAL_ID_FRAGMENT + ", " +
                "g." + GLOBAL_ID_OWNER_ID_FK + ", " +
                "o." + GLOBAL_ID_LOCAL_ID + " owner_" + GLOBAL_ID_LOCAL_ID + ", " +
                "o." + GLOBAL_ID_FRAGMENT + " owner_" + GLOBAL_ID_FRAGMENT + ", " +
                "o." + GLOBAL_ID_TYPE_NAME + " owner_" + GLOBAL_ID_TYPE_NAME
            )
            .from(
                tableNames.getSnapshotTableNameWithSchema() +
                " INNER JOIN " + tableNames.getCommitTableNameWithSchema() + " ON " + COMMIT_PK + " = " + SNAPSHOT_COMMIT_FK +
                " INNER JOIN " + tableNames.getGlobalIdTableNameWithSchema() + " g ON g." + GLOBAL_ID_PK + " = " + SNAPSHOT_GLOBAL_ID_FK +
                " LEFT OUTER JOIN " + tableNames.getGlobalIdTableNameWithSchema() + " o ON o." + GLOBAL_ID_PK + " = g." + GLOBAL_ID_OWNER_ID_FK)
            .queryName("snapshots");

        this.queryParams = queryParams;
        this.tableNameProvider = tableNames;
        applyQueryParams();
    }

    private void applyQueryParams() {
        queryParams.changedProperty().ifPresent(changedProperty -> {
            selectBuilder.and(SNAPSHOT_CHANGED, "like", stringParam("%\"" + queryParams.changedProperty().get() +"\"%"));
        });

        queryParams.from().ifPresent(from -> {
            selectBuilder.and(COMMIT_COMMIT_DATE, ">=", localDateTimeParam(from));
        });

        queryParams.to().ifPresent(to -> {
            selectBuilder.and(COMMIT_COMMIT_DATE, "<=", localDateTimeParam(to));
        });

        queryParams.toCommitId().ifPresent(commitId -> {
            selectBuilder.and(COMMIT_COMMIT_ID, "<=", bigDecimalParam(commitId.valueAsNumber()));
        });

        if (queryParams.commitIds().size() > 0) {
            selectBuilder.and(COMMIT_COMMIT_ID + " IN (" +
                    queryParams.commitIds()
                            .stream()
                            .map(c -> c.valueAsNumber().toString())
                            .collect(Collectors.joining(",")) +
                    ")");
        }

        queryParams.version().ifPresent(ver -> selectBuilder.and(SNAPSHOT_VERSION, ver));

        queryParams.author().ifPresent(author -> selectBuilder.and(COMMIT_AUTHOR, author));

        if (queryParams.commitProperties().size() > 0) {
            for (Map.Entry<String, String> commitProperty : queryParams.commitProperties().entrySet()) {
                addCommitPropertyFilter(selectBuilder, commitProperty.getKey(), commitProperty.getValue());
            }
        }

        queryParams.snapshotType().ifPresent(snapshotType -> selectBuilder.and(SNAPSHOT_TYPE, snapshotType.name()));
    }

    void addSnapshotPkFilter(long snapshotPk) {
        selectBuilder.and(SNAPSHOT_PK, snapshotPk);
    }

    void addGlobalIdFilter(long globalIdPk) {
        if (!queryParams.isAggregate()) {
            selectBuilder.and("g." + GLOBAL_ID_PK, globalIdPk);
        }
        else {
            selectBuilder.and("( g." + GLOBAL_ID_PK + " = ? OR g." + GLOBAL_ID_OWNER_ID_FK + " = ? )",
                    longParam(globalIdPk), longParam(globalIdPk));
        }
    }


    void addSnapshotIdentifiersFilter(List<SnapshotDbIdentifier> snapshotDbIdentifiers) {

        selectBuilder.append("and (");

        snapshotDbIdentifiers.forEach(si ->
            selectBuilder.append("("+SNAPSHOT_GLOBAL_ID_FK+" = ? AND "+SNAPSHOT_VERSION+" = ?) OR",
                                 longParam(si.getGlobalIdPk()), longParam(si.getVer()))
        );

        selectBuilder.append(" 1!=1)");
    }

    void addVoOwnerEntityFilter(String ownerTypeName, String fragment) {
        selectBuilder.and("o." + GLOBAL_ID_TYPE_NAME + " = ?", Parameter.stringParam(ownerTypeName))
                     .and("g." + GLOBAL_ID_FRAGMENT + " = ?", Parameter.stringParam(fragment));
    }

    void addManagedTypesFilter(Set<String> managedTypeNames) {
        String basePredicate = SNAPSHOT_MANAGED_TYPE + " in (" + ToStringBuilder.join(managedTypeNames) + ")";

        if (!queryParams.isAggregate()) {
            selectBuilder.and(basePredicate);
        }
        else {
            selectBuilder.and(
                "(  " + basePredicate +
                    "  OR g.owner_id_fk in ( "+
                    "     select g1." + GLOBAL_ID_PK + " from " + snapshotTableName() + " s1 "+
                    "     INNER JOIN " + globalIdTableName() + " g1 ON g1." + GLOBAL_ID_PK + "= s1."+ SNAPSHOT_GLOBAL_ID_FK +
                    "     and  s1." + basePredicate + ")"+
                ")");
        }
    }

    List<CdoSnapshotSerialized> run() {
        selectBuilder.orderByDesc(SNAPSHOT_PK);
        selectBuilder.limit(queryParams.limit(), queryParams.skip());
        return selectBuilder.executeQuery(cdoSnapshotMapper);
    }

    private void addCommitPropertyFilter(SelectBuilder selectBuilder, String propertyName, String propertyValue) {
        selectBuilder.and("EXISTS (" +
                " SELECT * FROM " + commitPropertyTableName() +
                " WHERE " + COMMIT_PROPERTY_COMMIT_FK + " = " + COMMIT_PK +
                " AND " + COMMIT_PROPERTY_NAME + " = ?" +
                " AND " + COMMIT_PROPERTY_VALUE + " = ?)",
                stringParam(propertyName), stringParam(propertyValue));
    }

    private static class CdoSnapshotMapper implements ObjectMapper<CdoSnapshotSerialized> {
        @Override
        public CdoSnapshotSerialized get(ResultSet resultSet) throws SQLException {
            return new CdoSnapshotSerialized()
                    .withCommitAuthor(resultSet.getString(COMMIT_AUTHOR))
                    .withCommitDate(resultSet.getTimestamp(COMMIT_COMMIT_DATE))
                    .withCommitDateInstant(resultSet.getString(COMMIT_COMMIT_DATE_INSTANT))
                    .withCommitId(resultSet.getBigDecimal(COMMIT_COMMIT_ID))
                    .withCommitPk(resultSet.getLong(COMMIT_PK))
                    .withVersion(resultSet.getLong(SNAPSHOT_VERSION))
                    .withSnapshotState(resultSet.getString(SNAPSHOT_STATE))
                    .withChangedProperties(resultSet.getString(SNAPSHOT_CHANGED))
                    .withSnapshotType(resultSet.getString(SNAPSHOT_TYPE))
                    .withGlobalIdFragment(resultSet.getString(GLOBAL_ID_FRAGMENT))
                    .withGlobalIdLocalId(resultSet.getString(GLOBAL_ID_LOCAL_ID))
                    .withGlobalIdTypeName(resultSet.getString(SNAPSHOT_MANAGED_TYPE))
                    .withOwnerGlobalIdFragment(resultSet.getString("owner_" + GLOBAL_ID_FRAGMENT))
                    .withOwnerGlobalIdLocalId(resultSet.getString("owner_" + GLOBAL_ID_LOCAL_ID))
                    .withOwnerGlobalIdTypeName(resultSet.getString("owner_" + GLOBAL_ID_TYPE_NAME));
        }
    }

    private String snapshotTableName() {
        return tableNameProvider.getSnapshotTableNameWithSchema();
    }

    private String globalIdTableName() {
        return tableNameProvider.getGlobalIdTableNameWithSchema();
    }

    private String commitPropertyTableName() {
        return tableNameProvider.getCommitPropertyTableNameWithSchema();
    }

    static class SnapshotDbIdentifier {
        private final long version;
        private final long globalIdPk;

        SnapshotDbIdentifier(SnapshotIdentifier snapshotIdentifier, long globalIdPk) {
            this.version = snapshotIdentifier.getVersion();
            this.globalIdPk = globalIdPk;
        }

        public long getGlobalIdPk() {
            return globalIdPk;
        }

        public long getVer() {
            return version;
        }
    }
}
