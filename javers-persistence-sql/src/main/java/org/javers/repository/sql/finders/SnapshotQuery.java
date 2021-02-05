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
import org.javers.repository.sql.schema.ColumnNameProvider;
import org.javers.repository.sql.schema.TableNameProvider;
import org.javers.repository.sql.session.ObjectMapper;
import org.javers.repository.sql.session.Parameter;
import org.javers.repository.sql.session.SelectBuilder;
import org.javers.repository.sql.session.Session;

class SnapshotQuery {
    private final QueryParams queryParams;
    private final SelectBuilder selectBuilder;
    private final TableNameProvider tableNameProvider;
    private final ColumnNameProvider columnNameProvider;
    private final CdoSnapshotMapper cdoSnapshotMapper;

    public SnapshotQuery(TableNameProvider tableNames, ColumnNameProvider columnNameProvider, QueryParams queryParams, Session session) {
        this.selectBuilder = session
            .select(
                columnNameProvider.getSnapshotStateName()+ ", " +
                columnNameProvider.getSnapshotTypeName() + ", " +
                columnNameProvider.getSnapshotVersionName() + ", " +
                columnNameProvider.getSnapshotChangedName() + ", " +
                columnNameProvider.getSnapshotManagedTypeName() + ", " +
                columnNameProvider.getCommitPKName() + ", " +
                columnNameProvider.getCommitAuthorName()+ ", " +
                columnNameProvider.getCommitDateName() + ", " +
                columnNameProvider.getCommitInstantName() + ", " +
                columnNameProvider.getCommitIdName() + ", " +
                "g." + columnNameProvider.getGlobalIdLocalIdName() + ", " +
                "g." + columnNameProvider.getGlobalIdFragmentName() + ", " +
                "g." + columnNameProvider.getGlobalIdOwnerIDFKName() + ", " +
                "o." + columnNameProvider.getGlobalIdLocalIdName() + " owner_" + columnNameProvider.getGlobalIdLocalIdName() + ", " +
                "o." + columnNameProvider.getGlobalIdFragmentName()+ " owner_" + columnNameProvider.getGlobalIdFragmentName() + ", " +
                "o." + columnNameProvider.getGlobalIdTypeName() + " owner_" + columnNameProvider.getGlobalIdTypeName()
            )
            .from(
                tableNames.getSnapshotTableNameWithSchema() +
                " INNER JOIN " + tableNames.getCommitTableNameWithSchema() + " ON " + columnNameProvider.getCommitPKName()+ " = " + columnNameProvider.getSnapshotCommitFKName() +
                " INNER JOIN " + tableNames.getGlobalIdTableNameWithSchema() + " g ON g." + columnNameProvider.getGlobalIdPKName() + " = " + columnNameProvider.getSnapshotGlobalIDName() +
                " LEFT OUTER JOIN " + tableNames.getGlobalIdTableNameWithSchema() + " o ON o." + columnNameProvider.getGlobalIdPKName() + " = g." + columnNameProvider.getGlobalIdOwnerIDFKName())
            .queryName("snapshots");

        this.queryParams = queryParams;
        this.tableNameProvider = tableNames;
        this.columnNameProvider = columnNameProvider;
        this.cdoSnapshotMapper = new CdoSnapshotMapper(columnNameProvider);
        applyQueryParams();
    }

    private void applyQueryParams() {
        if (queryParams.changedProperties().size() > 0) {
            selectBuilder.append("AND (" +
                    queryParams.changedProperties().stream()
                            .map(it -> columnNameProvider.getSnapshotChangedName()+ " LIKE '%" + it + "%'")
                            .collect(Collectors.joining(" OR ")) +
                    ")");
        }
        
        

        queryParams.from().ifPresent(from -> {
            selectBuilder.and(columnNameProvider.getCommitDateName(), ">=", localDateTimeParam(from));
        });
        
                
        queryParams.fromInstant().ifPresent(fromInstant -> {
            selectBuilder.and(columnNameProvider.getCommitInstantName(), ">=", instantParam(fromInstant));
        });

        queryParams.to().ifPresent(to -> {
            selectBuilder.and(columnNameProvider.getCommitDateName() , "<=", localDateTimeParam(to));
        });

        queryParams.toInstant().ifPresent(toInstant -> {
            selectBuilder.and(columnNameProvider.getCommitInstantName(), "<=", instantParam(toInstant));
        });

        queryParams.toCommitId().ifPresent(commitId -> {
            selectBuilder.and(columnNameProvider.getCommitIdName() , "<=", bigDecimalParam(commitId.valueAsNumber()));
        });

        if (queryParams.commitIds().size() > 0) {
            selectBuilder.and(columnNameProvider.getCommitIdName() + " IN (" +
                    queryParams.commitIds()
                            .stream()
                            .map(c -> c.valueAsNumber().toString())
                            .collect(Collectors.joining(",")) +
                    ")");
        }
        
        
        queryParams.version().ifPresent(ver -> selectBuilder.and(columnNameProvider.getSnapshotVersionName()  , ver));

        queryParams.author().ifPresent(author -> selectBuilder.and(columnNameProvider.getCommitAuthorName(), author));

        if (queryParams.commitProperties().size() > 0) {
            for (Map.Entry<String, String> commitProperty : queryParams.commitProperties().entrySet()) {
                addCommitPropertyFilter(selectBuilder, commitProperty.getKey(), commitProperty.getValue());
            }
        }
        
        queryParams.snapshotType().ifPresent(snapshotType -> selectBuilder.and(columnNameProvider.getSnapshotTypeName(), snapshotType.name()));
    }
    
    void addSnapshotPkFilter(long snapshotPk) {
        selectBuilder.and(columnNameProvider.getSnapshotPKName(), snapshotPk);
    }

    void addGlobalIdFilter(long globalIdPk) {
        if (!queryParams.isAggregate()) {
            selectBuilder.and("g." + columnNameProvider.getGlobalIdPKName(), globalIdPk);
        }
        else {
            selectBuilder.and("( g." + columnNameProvider.getGlobalIdPKName() + " = ? OR g." + columnNameProvider.getGlobalIdOwnerIDFKName() + " = ? )",
                    longParam(globalIdPk), longParam(globalIdPk));
        }
    }


    void addSnapshotIdentifiersFilter(List<SnapshotDbIdentifier> snapshotDbIdentifiers) {

        selectBuilder.append("and (");

        
        snapshotDbIdentifiers.forEach(si ->
            selectBuilder.append("("+columnNameProvider.getSnapshotGlobalIDName() +" = ? AND "+columnNameProvider.getSnapshotVersionName()+" = ?) OR",
                                 longParam(si.getGlobalIdPk()), longParam(si.getVer()))
        );

        selectBuilder.append(" 1!=1)");
    }

    
    void addVoOwnerEntityFilter(String ownerTypeName, String fragment) {
    	
        selectBuilder.and("o." + columnNameProvider.getGlobalIdTypeName() + " = ?", Parameter.stringParam(ownerTypeName))
                     .and("g." + columnNameProvider.getGlobalIdFragmentName() + " = ?", Parameter.stringParam(fragment));
    }

    void addManagedTypesFilter(Set<String> managedTypeNames) {
    	
        String basePredicate = columnNameProvider.getSnapshotManagedTypeName() + " in (" + ToStringBuilder.join(managedTypeNames) + ")";

        if (!queryParams.isAggregate()) {
            selectBuilder.and(basePredicate);
        }
        else {
            selectBuilder.and(
                "(  " + basePredicate +
                    "  OR g.owner_id_fk in ( "+
                    "     select g1." + columnNameProvider.getGlobalIdPKName() + " from " + snapshotTableName() + " s1 "+
                    "     INNER JOIN " + globalIdTableName() + " g1 ON g1." + columnNameProvider.getGlobalIdPKName() + "= s1."+ columnNameProvider.getSnapshotGlobalIDName() +
                    "     and  s1." + basePredicate + ")"+
                ")");
        }
    }

    List<CdoSnapshotSerialized> run() {
        selectBuilder.orderByDesc(columnNameProvider.getSnapshotPKName());
        selectBuilder.limit(queryParams.limit(), queryParams.skip());
        return selectBuilder.executeQuery(cdoSnapshotMapper);
    }

    private void addCommitPropertyFilter(SelectBuilder selectBuilder, String propertyName, String propertyValue) {
    	
    	selectBuilder.and("EXISTS (" +
                " SELECT * FROM " + commitPropertyTableName() +
                " WHERE " + columnNameProvider.getCommitPropertyCommitFKName() + " = " + columnNameProvider.getCommitPKName() +
                " AND " + columnNameProvider.getCommitPropertyName() + " = ?" + 
                " AND " + columnNameProvider.getCommitPropertyValueName() + " = ?)",
                stringParam(propertyName), stringParam(propertyValue));
    }

    private static class CdoSnapshotMapper implements ObjectMapper<CdoSnapshotSerialized> {
    	ColumnNameProvider columnNameProvider;
    	
    	public CdoSnapshotMapper(ColumnNameProvider columnNameProvider) {
    		this.columnNameProvider=columnNameProvider;
    	}
    	
        @Override
        public CdoSnapshotSerialized get(ResultSet resultSet) throws SQLException {
        	
            return new CdoSnapshotSerialized()
                    .withCommitAuthor(resultSet.getString(columnNameProvider.getCommitAuthorName()))
                    .withCommitDate(resultSet.getTimestamp(columnNameProvider.getCommitDateName()))
                    .withCommitDateInstant(resultSet.getString(columnNameProvider.getCommitInstantName()))
                    .withCommitId(resultSet.getBigDecimal(columnNameProvider.getCommitIdName()))
                    .withCommitPk(resultSet.getLong(columnNameProvider.getCommitPKName()))
                    .withVersion(resultSet.getLong(columnNameProvider.getSnapshotVersionName())) 
                    .withSnapshotState(resultSet.getString(columnNameProvider.getSnapshotStateName()))
                    .withChangedProperties(resultSet.getString(columnNameProvider.getSnapshotChangedName()))
                    .withSnapshotType(resultSet.getString(columnNameProvider.getSnapshotChangedName()))
                    .withGlobalIdFragment(resultSet.getString(columnNameProvider.getGlobalIdFragmentName()))
                    .withGlobalIdLocalId(resultSet.getString(columnNameProvider.getGlobalIdLocalIdName()))
                    .withGlobalIdTypeName(resultSet.getString(columnNameProvider.getSnapshotManagedTypeName()))
                    .withOwnerGlobalIdFragment(resultSet.getString("owner_" + columnNameProvider.getGlobalIdFragmentName()))
                    .withOwnerGlobalIdLocalId(resultSet.getString("owner_" + columnNameProvider.getGlobalIdLocalIdName()))
                    .withOwnerGlobalIdTypeName(resultSet.getString("owner_" + columnNameProvider.getGlobalIdTypeName()));
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
