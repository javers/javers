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
    
    private final String globalIdPKColumnName;
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
    
    private final String  primaryKeyIndicator;
    private final String  foreignKeyIndicator;
    private final String  sequenceIndicator;
    private final String  indexIndicator;
    private final boolean isSuffix;
    
        
    SqlRepositoryConfiguration(boolean globalIdCacheDisabled, 
							   String  schemaName,
							   boolean schemaManagementEnabled,
							   String  globalIdTableName,
							   String  commitTableName,
							   String  snapshotTableName, 
							   String  commitPropertyTableName,
							   String  globalIdPKColumnName,
							   String  globalIdLocalIdColumnName,
							   String  globalIdFragmentColumnName,
							   String  globalIdTypeNameColumnName,
							   String  globalIdOwnerIDFKColumnName,
							   String  commitPKColumnName,
							   String  commitAuthorColumnName,
							   String  commitCommitDateColumnName,
							   String  commitCommitDateInstantColumnName,
							   String  commitCommitIdColumName,
							   String  commitPropertyCommitFKColumnName,
							   String  commitPropertyNameColumnName,
							   String  commitPropertyValueColumnName,
							   String  snapshotPKColumnName,
							   String  snapshotCommitFKColumnName,
							   String  snapshotGlobalIdFKColumnName,
							   String  snapshotTypeColumnName,
							   String  snapshotVersionColumnName,
							   String  snapshotStateColumnName,
							   String  snapshotChangedColumnName,
							   String  snapshotManagedTypeColumnName,
							   String  primaryKeyIndicator,
							   String  foreignKeyIndicator,
							   String  sequenceIndicator,
							   String  indexIndicator,
							   boolean isSuffix
    						   ) {
		Validate.argumentCheck(schemaName == null || !schemaName.isEmpty(),"schemaName should be null or non-empty");
		
		this.globalIdCacheDisabled       = globalIdCacheDisabled;
		this.schemaName                  = schemaName;
		this.schemaManagementEnabled     = schemaManagementEnabled;
		this.globalIdTableName           = globalIdTableName;
		this.commitTableName             = commitTableName;
		this.snapshotTableName           = snapshotTableName;
		this.commitPropertyTableName     = commitPropertyTableName;
		this.globalIdPKColumnName        = globalIdPKColumnName;
		this.globalIdLocalIdColumnName   = globalIdLocalIdColumnName;
		this.globalIdFragmentColumnName  = globalIdFragmentColumnName;
		this.globalIdTypeNameColumnName  = globalIdTypeNameColumnName;
		this.globalIdOwnerIDFKColumnName = globalIdOwnerIDFKColumnName;
		
		this.commitPKColumnName                = commitPKColumnName;
		this.commitAuthorColumnName            = commitAuthorColumnName;
		this.commitCommitDateColumnName        = commitCommitDateColumnName;
		this.commitCommitDateInstantColumnName = commitCommitDateInstantColumnName;
		this.commitCommitIdColumName           = commitCommitIdColumName;
		this.commitPropertyCommitFKColumnName  = commitPropertyCommitFKColumnName;
		this.commitPropertyNameColumnName      = commitPropertyNameColumnName;
		this.commitPropertyValueColumnName     = commitPropertyValueColumnName;
		
		this.snapshotPKColumnName          = snapshotPKColumnName;
		this.snapshotCommitFKColumnName    = snapshotCommitFKColumnName;
		this.snapshotGlobalIdFKColumnName  = snapshotGlobalIdFKColumnName;
		this.snapshotTypeColumnName        = snapshotTypeColumnName;
		this.snapshotVersionColumnName     = snapshotVersionColumnName;
		this.snapshotStateColumnName       = snapshotStateColumnName;
		this.snapshotChangedColumnName     = snapshotChangedColumnName;
		this.snapshotManagedTypeColumnName = snapshotManagedTypeColumnName;
		
		this.primaryKeyIndicator = primaryKeyIndicator;
		this.foreignKeyIndicator = foreignKeyIndicator;
		this.sequenceIndicator   = sequenceIndicator;
		this.indexIndicator      = indexIndicator;
		this.isSuffix            = isSuffix;
	    
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

	public Optional<String> getPrimaryKeyIndicator() {
		return Optional.ofNullable(primaryKeyIndicator);
	}

	public Optional<String> getForeignKeyIndicator() {
		return Optional.ofNullable(foreignKeyIndicator);
	}

	public Optional<String> getSequenceIndicator() {
		return Optional.ofNullable(sequenceIndicator);
	}
	
	public Optional<String> getIndexIndicator() {
		return Optional.ofNullable(indexIndicator);
	}

	public Optional<Boolean> getIsSuffix() {
		return Optional.ofNullable(isSuffix);
	}

}
