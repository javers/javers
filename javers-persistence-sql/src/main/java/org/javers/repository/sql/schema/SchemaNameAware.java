package org.javers.repository.sql.schema;

import java.util.Optional;

public abstract class SchemaNameAware {
    private final DBNameProvider dbNameProvider;

    protected SchemaNameAware(DBNameProvider dbNameProvider) {
        this.dbNameProvider = dbNameProvider;
    }

    protected String getCommitTableNameWithSchema() {
        return dbNameProvider.getCommitTableNameWithSchema();
    }

    protected String getSnapshotTableNameWithSchema() {
        return dbNameProvider.getSnapshotTableNameWithSchema();
    }

    protected String getGlobalIdTableNameWithSchema() {
        return dbNameProvider.getGlobalIdTableNameWithSchema();
    }

    protected String getCommitPropertyTableNameWithSchema() {
        return dbNameProvider.getCommitPropertyTableNameWithSchema();
    }

    protected DBObjectName getGlobalIdTableName() {
        return dbNameProvider.getGlobalIdTableName();
    }

    protected DBObjectName getCommitTableName() {
        return dbNameProvider.getCommitTableName();
    }

    protected DBObjectName getCommitPropertyTableName() {
        return dbNameProvider.getCommitPropertyTableName();
    }

    protected DBObjectName getSnapshotTableName() {
        return dbNameProvider.getSnapshotTableName();
    }

    protected DBObjectName getCommitPkSeqName(){
        return dbNameProvider.getCommitPkSeqName();
    }

    protected DBObjectName getSnapshotTablePkSeqName(){
        return dbNameProvider.getSnapshotTablePkSeqName();
    }

    protected DBObjectName getGlobalIdPkSeqName() {
        return dbNameProvider.getGlobalIdPkSeqName();
    }

    protected Optional<String> getSchemaName() {
        return dbNameProvider.getSchemaName();
    }
    
    protected String getGlobalIdPKColumnName() {
        return dbNameProvider.getGlobalIdPKColumnName();
    }
    
    protected String getGlobalIdLocalIdColumnName() {
        return dbNameProvider.getGlobalIdLocalIdColumnName();
    }

    protected String getGlobalIdFragmentColumnName() {
        return dbNameProvider.getGlobalIdFragmentColumnName();
    }
    
    protected String getGlobalIdTypeNameColumnName() {
        return dbNameProvider.getGlobalIdTypeNameColumnName();
    }
    
    protected String getGlobalIdOwnerIDFKColumnName() {
        return dbNameProvider.getGlobalIdOwnerIDFKColumnName();
    }
    
    protected String getCommitPKColumnName() {
        return dbNameProvider.getCommitPKColumnName();
    }

    protected String getCommitAuthorColumnName() {
        return dbNameProvider.getCommitAuthorColumnName();
    }
    
    protected String getCommitCommitDateColumnName() {
        return dbNameProvider.getCommitCommitDateColumnName();
    }
    
    protected String getCommitCommitDateInstantColumnName() {
        return dbNameProvider.getCommitCommitDateInstantColumnName();
    }
    
    protected String getCommitCommitIdColumName() {
        return dbNameProvider.getCommitCommitIdColumName();
    }
    
    protected String getCommitPropertyCommitFKColumnName() {
        return dbNameProvider.getCommitPropertyCommitFKColumnName();
    }
    
    protected String getCommitPropertyNameColumnName() {
        return dbNameProvider.getCommitPropertyNameColumnName();
    }

    protected String getCommitPropertyValueColumnName() {
        return dbNameProvider.getCommitPropertyValueColumnName();
    }    
    
    protected String getSnapshotPKColumnName() {
        return dbNameProvider.getSnapshotPKColumnName();
    }

    protected String getSnapshotCommitFKColumnName() {
        return dbNameProvider.getSnapshotCommitFKColumnName();
    }
    
    protected String getSnapshotGlobalIdFKColumnName() {
        return dbNameProvider.getSnapshotGlobalIdFKColumnName();
    }
    
    protected String getSnapshotTypeColumnName() {
        return dbNameProvider.getSnapshotTypeColumnName();
    }
    
    protected String getSnapshotVersionColumnName() {
        return dbNameProvider.getSnapshotVersionColumnName();
    }

    protected String getSnapshotStateColumnName() {
        return dbNameProvider.getSnapshotStateColumnName();
    }
    
    protected String getSnapshotChangedColumnName() {
        return dbNameProvider.getSnapshotChangedColumnName();
    }
    
    protected String getSnapshotManagedTypeColumnName() {
        return dbNameProvider.getSnapshotManagedTypeColumnName();
    }
    
    protected String getPrimaryKeyIndicator() {
        return dbNameProvider.getPrimaryKeyIndicator();
    }
    
    protected String getForeignKeyIndicator() {
        return dbNameProvider.getForeignKeyIndicator();
    }
    
    protected String getSequenceIndicator() {
        return dbNameProvider.getSequenceIndicator();
    }
    
    protected String getIndexIndicator() {
        return dbNameProvider.getIndexIndicator();
    }
    
    protected Boolean getIsSuffix() {
        return dbNameProvider.getIsSuffix();
    }
}
