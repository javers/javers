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

    private final String globalIdSequenceName;
    private final String commitSequenceName;
    private final String snapshotSequenceName;

    SqlRepositoryConfiguration(boolean globalIdCacheDisabled, String schemaName,
                               boolean schemaManagementEnabled, String globalIdTableName,
                               String commitTableName, String snapshotTableName, String commitPropertyTableName,
                               String globalIdSequenceName, String commitSequenceName, String snapshotSequenceName
                               ) {
        Validate.argumentCheck(schemaName == null || !schemaName.isEmpty(),"schemaName should be null or non-empty");

        this.globalIdCacheDisabled = globalIdCacheDisabled;
        this.schemaName = schemaName;
        this.schemaManagementEnabled = schemaManagementEnabled;
        this.globalIdTableName = globalIdTableName;
        this.commitTableName = commitTableName;
        this.snapshotTableName = snapshotTableName;
        this.commitPropertyTableName = commitPropertyTableName;
        this.globalIdSequenceName = globalIdSequenceName;
        this.commitSequenceName = commitSequenceName;
        this.snapshotSequenceName = snapshotSequenceName;
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

    public Optional<String> getGlobalIdTableName() {
        return Optional.ofNullable(globalIdTableName);
    }

    public Optional<String> getCommitTableName() {
        return Optional.ofNullable(commitTableName);
    }

    public Optional<String> getSnapshotTableName() {
        return Optional.ofNullable(snapshotTableName);
    }

    public Optional<String> getCommitPropertyTableName() {
        return Optional.ofNullable(commitPropertyTableName);
    }

    public Optional<String> getGlobalIdSequenceName() {
        return Optional.ofNullable(globalIdSequenceName);
    }

    public Optional<String> getCommitSequenceName() {
        return Optional.ofNullable(commitSequenceName);
    }

    public Optional<String> getSnapshotSequenceName() {
        return Optional.ofNullable(snapshotSequenceName);
    }

}
