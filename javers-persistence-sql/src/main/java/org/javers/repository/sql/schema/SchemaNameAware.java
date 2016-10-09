package org.javers.repository.sql.schema;

/**
 * TODO should be replaced with Java8 interface with default impl
 */
public abstract class SchemaNameAware {
    private final TableNameProvider tableNameProvider;

    protected SchemaNameAware(TableNameProvider tableNameProvider) {
        this.tableNameProvider = tableNameProvider;
    }

    protected String getCommitTableNameWithSchema() {
        return tableNameProvider.getCommitTableNameWithSchema();
    }

    protected String getSnapshotTableNameWithSchema() {
        return tableNameProvider.getSnapshotTableNameWithSchema();
    }

    protected String getGlobalIdTableNameWithSchema() {
        return tableNameProvider.getGlobalIdTableNameWithSchema();
    }

    protected String getCommitPropertyTableNameWithSchema() {
        return tableNameProvider.getCommitPropertyTableNameWithSchema();
    }

    public DBObjectName getGlobalIdTableName() {
        return tableNameProvider.getGlobalIdTableName();
    }

    public DBObjectName getCommitTableName() {
        return tableNameProvider.getCommitTableName();
    }

    public DBObjectName getCommitPropertyTableName() {
        return tableNameProvider.getCommitPropertyTableName();
    }

    public DBObjectName getSnapshotTableName() {
        return tableNameProvider.getSnapshotTableName();
    }

    protected String getSequenceNameWithSchema(String pkColName){
        return tableNameProvider.getSequenceNameWithSchema(pkColName);
    }

    /**
     * used only by migration scripts
     */
    @Deprecated
    protected String getCdoClassTableNameWithSchema() {
        return tableNameProvider.getCdoClassTableNameWithSchema();
    }
}
