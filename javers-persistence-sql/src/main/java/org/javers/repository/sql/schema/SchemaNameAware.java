package org.javers.repository.sql.schema;

import java.util.Optional;

public abstract class SchemaNameAware {
    private final TableNameProvider tableNameProvider;
    private final ColumnNameProvider columnNameProvider;

    protected SchemaNameAware(TableNameProvider tableNameProvider, ColumnNameProvider columnNameProvider) {
        this.tableNameProvider = tableNameProvider;
        this.columnNameProvider = columnNameProvider;
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
    
    protected String getGlobalIdPKName() {
    	return columnNameProvider.getGlobalIdPKName();
    }
    
    protected String getGlobalIdLocalIdName() {
    	return columnNameProvider.getGlobalIdLocalIdName();
    }
    
    protected String getGlobalIdFragmentName() {
    	return columnNameProvider.getGlobalIdFragmentName();
    }
    
    protected String getGlobalIdTypeName() {
    	return columnNameProvider.getGlobalIdTypeName();
    }
    
    protected String getGlobalIdOwnerIDFKName() {
    	return columnNameProvider.getGlobalIdOwnerIDFKName();
    }
    
    protected String getCommitPKName() {
    	return columnNameProvider.getCommitPKName();
    }
    
    protected String getCommitAuthorName() {
    	return columnNameProvider.getCommitAuthorName();
    }
    
    protected String getCommitDateName() {
    	return columnNameProvider.getCommitDateName();
    }
    
    protected String getCommitInstantName() {
    	return columnNameProvider.getCommitInstantName();
    }
    
    protected String getCommitIdName() {
    	return columnNameProvider.getCommitIdName();
    }
    
    protected String getCommitPropertyCommitFKName() {
    	return columnNameProvider.getCommitPropertyCommitFKName();
    }
    
    protected String getCommitPropertyName() {
    	return columnNameProvider.getCommitPropertyName();
    }

    protected String getCommitPropertyValueName() {
    	return columnNameProvider.getCommitPropertyValueName();
    }
    
    protected String getSnapshotPKName() {
    	return columnNameProvider.getSnapshotPKName();
    }
    
    protected String getSnapshotCommitFKName() {
    	return columnNameProvider.getSnapshotCommitFKName();
    }
    
    protected String getSnapshotGlobalIDName() {
    	return columnNameProvider.getSnapshotGlobalIDName();
    }
    
    protected String getSnapshotTypeName() {
    	return columnNameProvider.getSnapshotTypeName();
    }

    protected String getSnapshotVersionName() {
    	return columnNameProvider.getSnapshotVersionName();
    }
    
    protected String getSnapshotStateName() {
    	return columnNameProvider.getSnapshotStateName();
    }
    
    protected String getSnapshotChangedName() {
    	return columnNameProvider.getSnapshotChangedName();
    }
    
    protected String getSnapshotManagedTypeName() {
    	return columnNameProvider.getSnapshotManagedTypeName();
    }
}
