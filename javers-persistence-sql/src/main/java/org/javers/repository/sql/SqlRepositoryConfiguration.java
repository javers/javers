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
    
    private final String globalIdPKColunmName;
    private final String globalIdLocalIdColumnName;
    private final String globalIdFragmentColumnName;
    private final String globalIdTypeNameColumnName;
    private final String globalIdOwnerIDFKColumnName;

    private final String commitPKColumnName;
    private final String commitAuthorColumnName;
    private final String commitCommitDateColumnName;
    private final String commitCommitDateInstantColumnName;
    private final String commitCommitIdColumName;
    private final String commitPropertyCommitFKColumnName;
    private final String commitPropertyNameColumnName;
    private final String commitPropertyValueColumnName;
    
    private final String snapshotPKColumnName;
    private final String snapshotCommitFKColumnName;
    private final String snapshotGlobalIdFKColumnName;
    private final String snapshotTypeColumnName;
    private final String snapshotVersionColumnName;
    private final String snapshotStateColumnName;
    private final String snapshotChangedColumnName;
    private final String snapshotManagedTypeColumnName;
    
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
        this.globalIdPKColunmName = null;
        this.globalIdLocalIdColumnName = null;
        this.globalIdFragmentColumnName = null;
        this.globalIdTypeNameColumnName = null;
        this.globalIdOwnerIDFKColumnName = null;
        
        this.commitPKColumnName = null;
        this.commitAuthorColumnName = null;
        this.commitCommitDateColumnName = null;
        this.commitCommitDateInstantColumnName = null;
        this.commitCommitIdColumName = null;
        this.commitPropertyCommitFKColumnName = null;
        this.commitPropertyNameColumnName = null;
        this.commitPropertyValueColumnName = null;
        
        this.snapshotPKColumnName = null;
        this.snapshotCommitFKColumnName = null;
        this.snapshotGlobalIdFKColumnName = null;
        this.snapshotTypeColumnName = null;
        this.snapshotVersionColumnName = null;
        this.snapshotStateColumnName = null;
        this.snapshotChangedColumnName = null;
        this.snapshotManagedTypeColumnName = null;
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

	public Optional<String> getGlobalIdPKColunmName() {
		return Optional.ofNullable(globalIdPKColunmName);
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

	public Optional<String> getGlobalIdOwnerIDFKColumnName() {
		return Optional.ofNullable(globalIdOwnerIDFKColumnName);
	}

	public Optional<String> getCommitPKColumnName() {
		return Optional.ofNullable(commitPKColumnName);
	}

	public Optional<String> getCommitAuthorColumnName() {
		return Optional.ofNullable(commitAuthorColumnName);
	}

	public Optional<String> getCommitCommitDateColumnName() {
		return Optional.ofNullable(commitCommitDateColumnName);
	}

	public Optional<String> getCommitCommitDateInstantColumnName() {
		return Optional.ofNullable(commitCommitDateInstantColumnName);
	}

	public Optional<String> getCommitCommitIdColumName() {
		return Optional.ofNullable(commitCommitIdColumName);
	}

	public Optional<String> getCommitPropertyCommitFKColumnName() {
		return Optional.ofNullable(commitPropertyCommitFKColumnName);
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

	public Optional<String> getSnapshotGlobalIdFKColumnName() {
		return Optional.ofNullable(snapshotGlobalIdFKColumnName);
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
}
