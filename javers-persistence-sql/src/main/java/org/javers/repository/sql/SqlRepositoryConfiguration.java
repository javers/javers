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
    private final String globalIdPKColumnName;
    private final String globalIdLocalIdColumnName;
    private final String globalIdFragmentColumnName;
    private final String globalIdTypeNameColumnName;
    private final String globalIdOwnerIdFKColumnName;

    private final String commitTableName;
    private final String commitIdPKColumnName;
    private final String commitAuthorColumnName;
    private final String commitDateColumnName;
    private final String commitInstantColumnName;
    private final String commitIdColumnName;
    
    
    private final String commitPropertyTableName;
    private final String commitPropertyFKColumnName;
    private final String commitPropertyNameColumnName;
    private final String commitPropertyValueColumnName;
    
    private final String snapshotTableName;
    private final String snapshotPKColumnName;
    private final String snapshotCommitFKColumnName;
    private final String snapshotGlobalIDColumnName;
    private final String snapshotTypeColumnName;
    private final String snapshotVersionColumnName;
    private final String snapshotStateColumnName;
    private final String snapshotChangedColumnName;
    private final String snapshotManagedTypeColumnName;

    
    
    SqlRepositoryConfiguration(boolean globalIdCacheDisabled,
            String schemaName,
            boolean schemaManagementEnabled,
            String globalIdTableName,
            String commitTableName,
            String snapshotTableName,
            String commitPropertyTableName,
            String globalIdPKColumnName,
            String globalIdLocalIdColumnName,
            String globalIdFragmentColumnName,
            String globalIdTypeNameColumnName,
            String globalIdOwnerIdFKColumnName,
            String commitIdPKColumnName,
            String commitAuthorColumnName,
            String commitDateColumnName,
            String commitInstantColumnName,
            String commitIdColumnName,
            String commitPropertyFKColumnName,
            String commitPropertyNameColumnName,
            String commitPropertyValueColumnName,
            String snapshotPKColumnName,
            String snapshotCommitFKColumnName,
            String snapshotGlobalIDColumnName,
            String snapshotTypeColumnName,
            String snapshotVersionColumnName,
            String snapshotStateColumnName,
            String snapshotChangedColumnName,
            String snapshotManagedTypeColumnName) {

    	Validate.argumentCheck(schemaName == null || !schemaName.isEmpty(),"schemaName should be null or non-empty");

    	this.globalIdCacheDisabled=globalIdCacheDisabled;
        this.schemaName=schemaName;
        this.schemaManagementEnabled=schemaManagementEnabled;

        this.globalIdTableName=globalIdTableName;
        this.globalIdPKColumnName=globalIdPKColumnName;
        this.globalIdLocalIdColumnName=globalIdLocalIdColumnName;
        this.globalIdFragmentColumnName=globalIdFragmentColumnName;
        this.globalIdTypeNameColumnName=globalIdTypeNameColumnName;
        this.globalIdOwnerIdFKColumnName=globalIdOwnerIdFKColumnName;

        this.commitTableName=commitTableName;
        this.commitIdPKColumnName=commitIdPKColumnName;
        this.commitAuthorColumnName=commitAuthorColumnName;
        this.commitDateColumnName=commitDateColumnName;
        this.commitInstantColumnName=commitInstantColumnName;
        this.commitIdColumnName=commitIdColumnName;
        
        
        this.commitPropertyTableName=commitPropertyTableName;
        this.commitPropertyFKColumnName=commitPropertyFKColumnName;
        this.commitPropertyNameColumnName=commitPropertyNameColumnName;
        this.commitPropertyValueColumnName=commitPropertyValueColumnName;
        
        this.snapshotTableName=snapshotTableName;
        this.snapshotPKColumnName=snapshotPKColumnName;
        this.snapshotCommitFKColumnName=snapshotCommitFKColumnName;
        this.snapshotGlobalIDColumnName=snapshotGlobalIDColumnName;
        this.snapshotTypeColumnName=snapshotTypeColumnName;
        this.snapshotVersionColumnName=snapshotVersionColumnName;
        this.snapshotStateColumnName=snapshotStateColumnName;
        this.snapshotChangedColumnName=snapshotChangedColumnName;
        this.snapshotManagedTypeColumnName=snapshotManagedTypeColumnName;
		

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

    public Optional<String> getGlobalIdPKColumnName() {
		return Optional.ofNullable(globalIdPKColumnName);
	}

	public Optional<String> getGlobalIdLocalIdColumnName() {
		return Optional.ofNullable(globalIdLocalIdColumnName);
	}

	public Optional<String> getGlobalIdFragmentColumnName() {
		return Optional.ofNullable(globalIdFragmentColumnName);
	}

	public Optional<String> getGlobalIdTypeNameColumnName() {
		return Optional.ofNullable(globalIdTypeNameColumnName);
	}

	public Optional<String> getGlobalIdOwnerIdFKColumnName() {
		return Optional.ofNullable(globalIdOwnerIdFKColumnName);
	}

	public Optional<String> getCommitIdPKColumnName() {
		return Optional.ofNullable(commitIdPKColumnName);
	}

	public Optional<String> getCommitAuthorColumnName() {
		return Optional.ofNullable(commitAuthorColumnName);
	}

	public Optional<String> getCommitDateColumnName() {
		return Optional.ofNullable(commitDateColumnName);
	}

	public Optional<String> getCommitInstantColumnName() {
		return Optional.ofNullable(commitInstantColumnName);
	}

	public Optional<String> getCommitIdColumnName() {
		return Optional.ofNullable(commitIdColumnName);
	}

	public Optional<String> getCommitPropertyFKColumnName() {
		return Optional.ofNullable(commitPropertyFKColumnName);
	}

	public Optional<String> getCommitPropertyNameColumnName() {
		return Optional.ofNullable(commitPropertyNameColumnName);
	}

	public Optional<String> getCommitPropertyValueColumnName() {
		return Optional.ofNullable(commitPropertyValueColumnName);
	}

	public Optional<String> getSnapshotPKColumnName() {
		return Optional.ofNullable(snapshotPKColumnName);
	}

	public Optional<String> getSnapshotCommitFKColumnName() {
		return Optional.ofNullable(snapshotCommitFKColumnName);
	}

	public Optional<String> getSnapshotGlobalIDColumnName() {
		return Optional.ofNullable(snapshotGlobalIDColumnName);
	}

	public Optional<String> getSnapshotTypeColumnName() {
		return Optional.ofNullable(snapshotTypeColumnName);
	}

	public Optional<String> getSnapshotVersionColumnName() {
		return Optional.ofNullable(snapshotVersionColumnName);
	}

	public Optional<String> getSnapshotStateColumnName() {
		return Optional.ofNullable(snapshotStateColumnName);
	}

	public Optional<String> getSnapshotChangedColumnName() {
		return Optional.ofNullable(snapshotChangedColumnName);
	}

	public Optional<String> getSnapshotManagedTypeColumnName() {
		return Optional.ofNullable(snapshotManagedTypeColumnName);
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
}
