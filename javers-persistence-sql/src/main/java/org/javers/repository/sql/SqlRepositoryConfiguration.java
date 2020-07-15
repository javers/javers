package org.javers.repository.sql;

import java.util.Optional;
import org.javers.common.validation.Validate;


/**
 * @author bartosz.walacik
 */
public class SqlRepositoryConfiguration {
    private final boolean globalIdCacheDisabled;
    private final String schemaName;
    private final boolean schemaManagementEnabled;

    private final String globalIdTableName;
    private final String commitTableName;
    private final String snapshotTableName;
    private final String commitPropertyTableName;

    SqlRepositoryConfiguration(boolean globalIdCacheDisabled, String schemaName,
                                      boolean schemaManagementEnabled, String globalIdTableName,
                                      String commitTableName,
                                      String snapshotTableName, String commitPropertyTableName) {
        Validate.argumentCheck(schemaName == null || !schemaName.isEmpty(),"schemaName should be null or non-empty");

        this.globalIdCacheDisabled = globalIdCacheDisabled;
        this.schemaName = schemaName;
        this.schemaManagementEnabled = schemaManagementEnabled;
        this.globalIdTableName = globalIdTableName;
        this.commitTableName = commitTableName;
        this.snapshotTableName = snapshotTableName;
        this.commitPropertyTableName = commitPropertyTableName;
    }

    public boolean isGlobalIdCacheDisabled() {
        return globalIdCacheDisabled;
    }

    /**
     * null or non-empty
     */
    public String getSchemaName() {
        return schemaName;
    }

    public Optional<String> getSchemaNameAsOptional() {
        return Optional.ofNullable(schemaName);
    }

    public boolean isSchemaManagementEnabled() {
        return schemaManagementEnabled;
    }

    public Optional<String> getGlobalIdTableNameAsOptional() {
        return Optional.ofNullable(globalIdTableName);
    }

    public Optional<String> getCommitTableNameAsOptional() {
        return Optional.ofNullable(commitTableName);
    }

    public Optional<String> getSnapshotTableNameAsOptional() {
        return Optional.ofNullable(snapshotTableName);
    }

    public Optional<String> getCommitPropertyTableNameAsOptional() {
        return Optional.ofNullable(commitPropertyTableName);
    }
}
