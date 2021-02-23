package org.javers.repository.sql.finders;

import static org.javers.repository.sql.session.Parameter.bigDecimalParam;
import static org.javers.repository.sql.session.Parameter.instantParam;
import static org.javers.repository.sql.session.Parameter.localDateTimeParam;
import static org.javers.repository.sql.session.Parameter.longParam;
import static org.javers.repository.sql.session.Parameter.stringParam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javers.common.string.ToStringBuilder;
import org.javers.core.json.CdoSnapshotSerialized;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.SnapshotIdentifier;
import org.javers.repository.sql.schema.DBNameProvider;
import org.javers.repository.sql.session.ObjectMapper;
import org.javers.repository.sql.session.Parameter;
import org.javers.repository.sql.session.SelectBuilder;
import org.javers.repository.sql.session.Session;

class SnapshotQuery {
    private final QueryParams queryParams;
    private final SelectBuilder selectBuilder;
    private static DBNameProvider dbNameProvider = null;
    private final CdoSnapshotMapper cdoSnapshotMapper = new CdoSnapshotMapper();

    public SnapshotQuery(DBNameProvider dbNames, QueryParams queryParams, Session session) {
        this.selectBuilder = session
            .select(
                dbNames.getSnapshotStateColumnName() + ", " +
                dbNames.getSnapshotTypeColumnName() + ", " +
                dbNames.getSnapshotVersionColumnName() + ", " +
                dbNames.getSnapshotChangedColumnName() + ", " +
                dbNames.getSnapshotManagedTypeColumnName() + ", " +
                " com."+dbNames.getCommitPKColumnName() + ", " +
                dbNames.getCommitAuthorColumnName() + ", " +
                dbNames.getCommitCommitDateColumnName()+ ", " +
                dbNames.getCommitCommitDateInstantColumnName()+ ", " +
                dbNames.getCommitCommitIdColumName()+ ", " +
                "g." + dbNames.getGlobalIdLocalIdColumnName() + ", " +
                "g." + dbNames.getGlobalIdFragmentColumnName()+ ", " +
                "g." + dbNames.getGlobalIdOwnerIDFKColumnName() + ", " +
                "o." + dbNames.getGlobalIdLocalIdColumnName() + " owner_" + dbNames.getGlobalIdLocalIdColumnName() + ", " +
                "o." + dbNames.getGlobalIdFragmentColumnName()  + " owner_" + dbNames.getGlobalIdFragmentColumnName() + ", " +
                "o." + dbNames.getGlobalIdTypeNameColumnName()  + " owner_" + dbNames.getGlobalIdTypeNameColumnName()
            )
            .from(
            		dbNames.getSnapshotTableNameWithSchema() + " snap " +
                " INNER JOIN " + dbNames.getCommitTableNameWithSchema() + " com ON com." + dbNames.getCommitPKColumnName() + " = snap." + dbNames.getSnapshotCommitFKColumnName() +
                " INNER JOIN " + dbNames.getGlobalIdTableNameWithSchema() + " g ON g." + dbNames.getGlobalIdPKColumnName() + " = snap." + dbNames.getSnapshotGlobalIdFKColumnName() +
                " LEFT OUTER JOIN " + dbNames.getGlobalIdTableNameWithSchema() + " o ON o." + dbNames.getGlobalIdPKColumnName() + " = g." + dbNames.getGlobalIdOwnerIDFKColumnName())
            .queryName("snapshots");

        this.queryParams = queryParams;
        this.dbNameProvider = dbNames;
        applyQueryParams();
    }

    private void applyQueryParams() {
        if (queryParams.changedProperties().size() > 0) {
            selectBuilder.append("AND (" +
                    queryParams.changedProperties().stream()
                            .map(it -> dbNameProvider.getSnapshotChangedColumnName() + " LIKE '%" + it + "%'")
                            .collect(Collectors.joining(" OR ")) +
                    ")");
        }
        
        queryParams.from().ifPresent(from -> {
            selectBuilder.and(dbNameProvider.getCommitCommitDateColumnName()  , ">=", localDateTimeParam(from));
        });
        
        queryParams.fromInstant().ifPresent(fromInstant -> {
            selectBuilder.and(dbNameProvider.getCommitCommitDateInstantColumnName(), ">=", instantParam(fromInstant));
        });
        
        queryParams.to().ifPresent(to -> {
            selectBuilder.and(dbNameProvider.getCommitCommitDateColumnName(), "<=", localDateTimeParam(to));
        });
        
        queryParams.toInstant().ifPresent(toInstant -> {
            selectBuilder.and(dbNameProvider.getCommitCommitDateInstantColumnName(), "<=", instantParam(toInstant));
        });

        queryParams.toCommitId().ifPresent(commitId -> {
            selectBuilder.and(dbNameProvider.getCommitCommitIdColumName(), "<=", bigDecimalParam(commitId.valueAsNumber()));
        });

        if (queryParams.commitIds().size() > 0) {
            selectBuilder.and(dbNameProvider.getCommitCommitIdColumName() + " IN (" +
                    queryParams.commitIds()
                            .stream()
                            .map(c -> c.valueAsNumber().toString())
                            .collect(Collectors.joining(",")) +
                    ")");
        }
        
        
        queryParams.version().ifPresent(ver -> selectBuilder.and(dbNameProvider.getSnapshotVersionColumnName(), ver));
        
        queryParams.author().ifPresent(author -> selectBuilder.and(dbNameProvider.getCommitAuthorColumnName(), author));

        if (queryParams.commitProperties().size() > 0) {
            for (Map.Entry<String, String> commitProperty : queryParams.commitProperties().entrySet()) {
                addCommitPropertyFilter(selectBuilder, commitProperty.getKey(), commitProperty.getValue());
            }
        }

        queryParams.snapshotType().ifPresent(snapshotType -> selectBuilder.and(dbNameProvider.getSnapshotTypeColumnName(), snapshotType.name()));
    }

    void addSnapshotPkFilter(long snapshotPk) {
        selectBuilder.and(dbNameProvider.getSnapshotPKColumnName(), snapshotPk);
    }

    void addGlobalIdFilter(long globalIdPk) {
        if (!queryParams.isAggregate()) {
            selectBuilder.and("g." + dbNameProvider.getGlobalIdPKColumnName(), globalIdPk);
        }
        else {
            selectBuilder.and("( g." + dbNameProvider.getGlobalIdPKColumnName() + " = ? OR g." + dbNameProvider.getGlobalIdOwnerIDFKColumnName() + " = ? )",
                    longParam(globalIdPk), longParam(globalIdPk));
        }
    }


    void addSnapshotIdentifiersFilter(List<SnapshotDbIdentifier> snapshotDbIdentifiers) {

        selectBuilder.append("and (");

        snapshotDbIdentifiers.forEach(si ->
            selectBuilder.append("( snap."+dbNameProvider.getSnapshotGlobalIdFKColumnName()+" = ? AND "+ dbNameProvider.getSnapshotVersionColumnName()+" = ?) OR",
                                 longParam(si.getGlobalIdPk()), longParam(si.getVer()))
        );

        selectBuilder.append(" 1!=1)");
    }

    void addVoOwnerEntityFilter(String ownerTypeName, String fragment) {
        selectBuilder.and("o." + dbNameProvider.getGlobalIdTypeNameColumnName() + " = ?", Parameter.stringParam(ownerTypeName))
                     .and("g." + dbNameProvider.getGlobalIdFragmentColumnName() + " = ?", Parameter.stringParam(fragment));
    }

    void addManagedTypesFilter(Set<String> managedTypeNames) {
        String basePredicate = dbNameProvider.getSnapshotManagedTypeColumnName() + " in (" + ToStringBuilder.join(managedTypeNames) + ")";

        if (!queryParams.isAggregate()) {
            selectBuilder.and(basePredicate);
        }
        else {
            selectBuilder.and(
                "(  " + basePredicate +
                    "  OR g.owner_id_fk in ( "+
                    "     select g1." + dbNameProvider.getGlobalIdPKColumnName() + " from " + snapshotTableName() + " s1 "+
                    "     INNER JOIN " + globalIdTableName() + " g1 ON g1." + dbNameProvider.getGlobalIdPKColumnName() + "= s1."+ dbNameProvider.getSnapshotGlobalIdFKColumnName() +
                    "     and  s1." + basePredicate + ")"+
                ")");
        }
    }

    List<CdoSnapshotSerialized> run() {
        selectBuilder.orderByDesc(dbNameProvider.getSnapshotPKColumnName());
        selectBuilder.limit(queryParams.limit(), queryParams.skip());
        return selectBuilder.executeQuery(cdoSnapshotMapper);
    }

    private void addCommitPropertyFilter(SelectBuilder selectBuilder, String propertyName, String propertyValue) {
        selectBuilder.and("EXISTS (" +
                " SELECT * FROM " + commitPropertyTableName() +
                " WHERE " + dbNameProvider.getCommitPropertyCommitFKColumnName() + " = " + dbNameProvider.getCommitPKColumnName() +
                " AND " + dbNameProvider.getCommitPropertyNameColumnName() + " = ?" +
                " AND " + dbNameProvider.getCommitPropertyValueColumnName() + " = ?)",
                stringParam(propertyName), stringParam(propertyValue));
    }

    private static class CdoSnapshotMapper implements ObjectMapper<CdoSnapshotSerialized> {
        @Override
      
        
        public CdoSnapshotSerialized get(ResultSet resultSet) throws SQLException {
            return new CdoSnapshotSerialized()
                    .withCommitAuthor(resultSet.getString(dbNameProvider.getCommitAuthorColumnName()))
                    .withCommitDate(resultSet.getTimestamp(dbNameProvider.getCommitCommitDateColumnName()))
                    .withCommitDateInstant(resultSet.getString(dbNameProvider.getCommitCommitDateInstantColumnName()))
                    .withCommitId(resultSet.getBigDecimal(dbNameProvider.getCommitCommitIdColumName()))
                    .withCommitPk(resultSet.getLong(dbNameProvider.getCommitPKColumnName()))
                    .withVersion(resultSet.getLong(dbNameProvider.getSnapshotVersionColumnName()))
                    .withSnapshotState(resultSet.getString(dbNameProvider.getSnapshotStateColumnName()))
                    .withChangedProperties(resultSet.getString(dbNameProvider.getSnapshotChangedColumnName()))
                    .withSnapshotType(resultSet.getString(dbNameProvider.getSnapshotTypeColumnName()))
                    .withGlobalIdFragment(resultSet.getString(dbNameProvider.getGlobalIdFragmentColumnName()))
                    .withGlobalIdLocalId(resultSet.getString(dbNameProvider.getGlobalIdLocalIdColumnName()))
                    .withGlobalIdTypeName(resultSet.getString(dbNameProvider.getSnapshotManagedTypeColumnName()))
                    .withOwnerGlobalIdFragment(resultSet.getString("owner_" + dbNameProvider.getGlobalIdFragmentColumnName()))
                    .withOwnerGlobalIdLocalId(resultSet.getString("owner_" + dbNameProvider.getGlobalIdLocalIdColumnName()))
                    .withOwnerGlobalIdTypeName(resultSet.getString("owner_" + dbNameProvider.getGlobalIdTypeNameColumnName()));
        }
    }

    private String snapshotTableName() {
        return dbNameProvider.getSnapshotTableNameWithSchema();
    }

    private String globalIdTableName() {
        return dbNameProvider.getGlobalIdTableNameWithSchema();
    }

    private String commitPropertyTableName() {
        return dbNameProvider.getCommitPropertyTableNameWithSchema();
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