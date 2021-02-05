package org.javers.spring.boot.sql;

import org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook;
import org.javers.spring.JaversSpringProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "javers")
public class JaversSqlProperties extends JaversSpringProperties {
    private static final String DEFAULT_OBJECT_ACCESS_HOOK = HibernateUnproxyObjectAccessHook.class.getName();

    private boolean sqlSchemaManagementEnabled = true;
    private boolean sqlGlobalIdCacheDisabled = false;
    private String sqlSchema;

	private String sqlGlobalIdTableName;
    private String sqlGlobalIdPKColumnName;
    private String sqlGlobalIdLocalIdColumnName;
    private String sqlGlobalIdFragmentColumnName;
    private String sqlGlobalIdTypeNameColumnName;
    private String sqlGlobalIdOwnerIdFKColumnName;

    private String sqlCommitTableName;
    private String sqlCommitIdPKColumnName;
    private String sqlCommitAuthorColumnName;
    private String sqlCommitDateColumnName;
    private String sqlCommitInstantColumnName;
    private String sqlCommitIdColumnName;
    
    private String sqlCommitPropertyTableName;
    private String sqlCommitPropertyFKColumnName;
    private String sqlCommitPropertyNameColumnName;
    private String sqlCommitPropertyValueColumnName;
    
    private String sqlSnapshotTableName;
    private String sqlSnapshotPKColumnName;
    private String sqlSnapshotCommitFKColumnName;
    private String sqlSnapshotGlobalIDColumnName;
    private String sqlSnapshotTypeColumnName;
    private String sqlSnapshotVersionColumnName;
    private String sqlSnapshotStateColumnName;
    private String sqlSnapshotChangedColumnName;
    private String sqlSnapshotManagedTypeColumnName;


    public boolean isSqlSchemaManagementEnabled() {
        return sqlSchemaManagementEnabled;
    }

    public void setSqlSchemaManagementEnabled(boolean sqlSchemaManagementEnabled) {
        this.sqlSchemaManagementEnabled = sqlSchemaManagementEnabled;
    }

    public String getSqlSchema() {
        return sqlSchema;
    }

    public void setSqlSchema(String sqlSchema) {
        this.sqlSchema = sqlSchema;
    }

    public boolean isSqlGlobalIdCacheDisabled() {
        return sqlGlobalIdCacheDisabled;
    }

    public void setSqlGlobalIdCacheDisabled(boolean sqlGlobalIdCacheDisabled) {
        this.sqlGlobalIdCacheDisabled = sqlGlobalIdCacheDisabled;
    }

    protected String defaultObjectAccessHook(){
        return DEFAULT_OBJECT_ACCESS_HOOK;
    }

    public String getSqlGlobalIdTableName() {
        return sqlGlobalIdTableName;
    }

    public void setSqlGlobalIdTableName(String sqlGlobalIdTableName) {
        this.sqlGlobalIdTableName = sqlGlobalIdTableName;
    }

    public String getSqlCommitTableName() {
        return sqlCommitTableName;
    }

    public void setSqlCommitTableName(String sqlCommitTableName) {
        this.sqlCommitTableName = sqlCommitTableName;
    }

    public String getSqlSnapshotTableName() {
        return sqlSnapshotTableName;
    }

    public void setSqlSnapshotTableName(String sqlSnapshotTableName) {
        this.sqlSnapshotTableName = sqlSnapshotTableName;
    }

    public String getSqlCommitPropertyTableName() {
        return sqlCommitPropertyTableName;
    }

    public void setSqlCommitPropertyTableName(String sqlCommitPropertyTableName) {
        this.sqlCommitPropertyTableName = sqlCommitPropertyTableName;
    }
    

    public String getSqlGlobalIdPKColumnName() {
		return sqlGlobalIdPKColumnName;
	}

	public String getSqlGlobalIdLocalIdColumnName() {
		return sqlGlobalIdLocalIdColumnName;
	}

	public String getSqlGlobalIdFragmentColumnName() {
		return sqlGlobalIdFragmentColumnName;
	}

	public String getSqlGlobalIdTypeNameColumnName() {
		return sqlGlobalIdTypeNameColumnName;
	}

	public String getSqlGlobalIdOwnerIdFKColumnName() {
		return sqlGlobalIdOwnerIdFKColumnName;
	}

	public String getSqlCommitIdPKColumnName() {
		return sqlCommitIdPKColumnName;
	}

	public String getSqlCommitAuthorColumnName() {
		return sqlCommitAuthorColumnName;
	}

	public String getSqlCommitDateColumnName() {
		return sqlCommitDateColumnName;
	}

	public String getSqlCommitInstantColumnName() {
		return sqlCommitInstantColumnName;
	}

	public String getSqlCommitIdColumnName() {
		return sqlCommitIdColumnName;
	}

	public String getSqlCommitPropertyFKColumnName() {
		return sqlCommitPropertyFKColumnName;
	}

	public String getSqlCommitPropertyNameColumnName() {
		return sqlCommitPropertyNameColumnName;
	}

	public String getSqlCommitPropertyValueColumnName() {
		return sqlCommitPropertyValueColumnName;
	}

	public String getSqlSnapshotPKColumnName() {
		return sqlSnapshotPKColumnName;
	}

	public String getSqlSnapshotCommitFKColumnName() {
		return sqlSnapshotCommitFKColumnName;
	}

	public String getSqlSnapshotGlobalIDColumnName() {
		return sqlSnapshotGlobalIDColumnName;
	}

	public String getSqlSnapshotTypeColumnName() {
		return sqlSnapshotTypeColumnName;
	}

	public String getSqlSnapshotVersionColumnName() {
		return sqlSnapshotVersionColumnName;
	}

	public String getSqlSnapshotStateColumnName() {
		return sqlSnapshotStateColumnName;
	}

	public String getSqlSnapshotChangedColumnName() {
		return sqlSnapshotChangedColumnName;
	}

	public String getSqlSnapshotManagedTypeColumnName() {
		return sqlSnapshotManagedTypeColumnName;
	}

	public void setSqlGlobalIdPKColumnName(String sqlGlobalIdPKColumnName) {
		this.sqlGlobalIdPKColumnName = sqlGlobalIdPKColumnName;
	}

	public void setSqlGlobalIdLocalIdColumnName(String sqlGlobalIdLocalIdColumnName) {
		this.sqlGlobalIdLocalIdColumnName = sqlGlobalIdLocalIdColumnName;
	}

	public void setSqlGlobalIdFragmentColumnName(String sqlGlobalIdFragmentColumnName) {
		this.sqlGlobalIdFragmentColumnName = sqlGlobalIdFragmentColumnName;
	}

	public void setSqlGlobalIdTypeNameColumnName(String sqlGlobalIdTypeNameColumnName) {
		this.sqlGlobalIdTypeNameColumnName = sqlGlobalIdTypeNameColumnName;
	}

	public void setSqlGlobalIdOwnerIdFKColumnName(String sqlGlobalIdOwnerIdFKColumnName) {
		this.sqlGlobalIdOwnerIdFKColumnName = sqlGlobalIdOwnerIdFKColumnName;
	}

	public void setSqlCommitIdPKColumnName(String sqlCommitIdPKColumnName) {
		this.sqlCommitIdPKColumnName = sqlCommitIdPKColumnName;
	}

	public void setSqlCommitAuthorColumnName(String sqlCommitAuthorColumnName) {
		this.sqlCommitAuthorColumnName = sqlCommitAuthorColumnName;
	}

	public void setSqlCommitDateColumnName(String sqlCommitDateColumnName) {
		this.sqlCommitDateColumnName = sqlCommitDateColumnName;
	}

	public void setSqlCommitInstantColumnName(String sqlCommitInstantColumnName) {
		this.sqlCommitInstantColumnName = sqlCommitInstantColumnName;
	}

	public void setSqlCommitIdColumnName(String sqlCommitIdColumnName) {
		this.sqlCommitIdColumnName = sqlCommitIdColumnName;
	}

	public void setSqlCommitPropertyFKColumnName(String sqlCommitPropertyFKColumnName) {
		this.sqlCommitPropertyFKColumnName = sqlCommitPropertyFKColumnName;
	}

	public void setSqlCommitPropertyNameColumnName(String sqlCommitPropertyNameColumnName) {
		this.sqlCommitPropertyNameColumnName = sqlCommitPropertyNameColumnName;
	}

	public void setSqlCommitPropertyValueColumnName(String sqlCommitPropertyValueColumnName) {
		this.sqlCommitPropertyValueColumnName = sqlCommitPropertyValueColumnName;
	}

	public void setSqlSnapshotPKColumnName(String sqlSnapshotPKColumnName) {
		this.sqlSnapshotPKColumnName = sqlSnapshotPKColumnName;
	}

	public void setSqlSnapshotCommitFKColumnName(String sqlSnapshotCommitFKColumnName) {
		this.sqlSnapshotCommitFKColumnName = sqlSnapshotCommitFKColumnName;
	}

	public void setSqlSnapshotGlobalIDColumnName(String sqlSnapshotGlobalIDColumnName) {
		this.sqlSnapshotGlobalIDColumnName = sqlSnapshotGlobalIDColumnName;
	}

	public void setSqlSnapshotTypeColumnName(String sqlSnapshotTypeColumnName) {
		this.sqlSnapshotTypeColumnName = sqlSnapshotTypeColumnName;
	}

	public void setSqlSnapshotVersionColumnName(String sqlSnapshotVersionColumnName) {
		this.sqlSnapshotVersionColumnName = sqlSnapshotVersionColumnName;
	}

	public void setSqlSnapshotStateColumnName(String sqlSnapshotStateColumnName) {
		this.sqlSnapshotStateColumnName = sqlSnapshotStateColumnName;
	}

	public void setSqlSnapshotChangedColumnName(String sqlSnapshotChangedColumnName) {
		this.sqlSnapshotChangedColumnName = sqlSnapshotChangedColumnName;
	}

	public void setSqlSnapshotManagedTypeColumnName(String sqlSnapshotManagedTypeColumnName) {
		this.sqlSnapshotManagedTypeColumnName = sqlSnapshotManagedTypeColumnName;
	}

}
