package org.javers.repository.sql.schema;

import java.util.Optional;

public abstract class SchemaNameAware {
    private final DBNameProvider tableNameProvider;

    protected SchemaNameAware(DBNameProvider tableNameProvider) {
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

    protected DBObjectName getGlobalIdTableName() {
        return tableNameProvider.getGlobalIdTableName();
    }

    protected DBObjectName getCommitTableName() {
        return tableNameProvider.getCommitTableName();
    }

    protected DBObjectName getCommitPropertyTableName() {
        return tableNameProvider.getCommitPropertyTableName();
    }

    protected DBObjectName getSnapshotTableName() {
        return tableNameProvider.getSnapshotTableName();
    }

    protected DBObjectName getCommitPkSeqName(){
        return tableNameProvider.getCommitPkSeqName();
    }

    protected DBObjectName getSnapshotTablePkSeqName(){
        return tableNameProvider.getSnapshotTablePkSeqName();
    }

    protected DBObjectName getGlobalIdPkSeqName() {
        return tableNameProvider.getGlobalIdPkSeqName();
    }

    protected Optional<String> getSchemaName() {
        return tableNameProvider.getSchemaName();
    }
}
