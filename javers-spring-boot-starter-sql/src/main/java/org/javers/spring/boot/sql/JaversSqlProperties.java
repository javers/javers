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
    private String sqlCommitTableName;
    private String sqlSnapshotTableName;
    private String sqlCommitPropertyTableName;
    
    private String sqlGlobalIdPKColumnName;
    private String sqlGlobalIdLocalIdColumnName;
    private String sqlGlobalIdFragmentColumnName;
    private String sqlGlobalIdTypeNameColumnName;
    private String sqlGlobalIdOwnerIDFKColumnName;

    private String sqlCommitPKColumnName;
    private String sqlCommitAuthorColumnName;
    private String sqlCommitCommitDateColumnName;
    private String sqlCommitCommitDateInstantColumnName;
    private String sqlCommitCommitIdColumName;
    private String sqlCommitPropertyCommitFKColumnName;
    private String sqlCommitPropertyNameColumnName;
    private String sqlCommitPropertyValueColumnName;
    
    private String sqlSnapshotPKColumnName;
    private String sqlSnapshotCommitFKColumnName;
    private String sqlSnapshotGlobalIdFKColumnName;
    private String sqlSnapshotTypeColumnName;
    private String sqlSnapshotVersionColumnName;
    private String sqlSnapshotStateColumnName;
    private String sqlSnapshotChangedColumnName;
    private String sqlSnapshotManagedTypeColumnName;

	private String  sqlPrimaryKeyIndicator;
	private String  sqlForeignKeyIndicator;
	private String  sqlSequenceIndicator;
	private String  sqlIndexIndicator;
	private boolean sqlIsSuffix = true;

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

		public void setSqlGlobalIdPKColumnName(String sqlGlobalIdPKColumnName) {
			this.sqlGlobalIdPKColumnName = sqlGlobalIdPKColumnName;
		}

		public String getSqlGlobalIdLocalIdColumnName() {
			return sqlGlobalIdLocalIdColumnName;
		}

		public void setSqlGlobalIdLocalIdColumnName(String sqlGlobalIdLocalIdColumnName) {
			this.sqlGlobalIdLocalIdColumnName = sqlGlobalIdLocalIdColumnName;
		}

		public String getSqlGlobalIdFragmentColumnName() {
			return sqlGlobalIdFragmentColumnName;
		}

		public void setSqlGlobalIdFragmentColumnName(String sqlGlobalIdFragmentColumnName) {
			this.sqlGlobalIdFragmentColumnName = sqlGlobalIdFragmentColumnName;
		}

		public String getSqlGlobalIdTypeNameColumnName() {
			return sqlGlobalIdTypeNameColumnName;
		}

		public void setSqlGlobalIdTypeNameColumnName(String sqlGlobalIdTypeNameColumnName) {
			this.sqlGlobalIdTypeNameColumnName = sqlGlobalIdTypeNameColumnName;
		}

		public String getSqlGlobalIdOwnerIDFKColumnName() {
			return sqlGlobalIdOwnerIDFKColumnName;
		}

		public void setSqlGlobalIdOwnerIDFKColumnName(String sqlGlobalIdOwnerIDFKColumnName) {
			this.sqlGlobalIdOwnerIDFKColumnName = sqlGlobalIdOwnerIDFKColumnName;
		}

		public String getSqlCommitPKColumnName() {
			return sqlCommitPKColumnName;
		}

		public void setSqlCommitPKColumnName(String sqlCommitPKColumnName) {
			this.sqlCommitPKColumnName = sqlCommitPKColumnName;
		}

		public String getSqlCommitAuthorColumnName() {
			return sqlCommitAuthorColumnName;
		}

		public void setSqlCommitAuthorColumnName(String sqlCommitAuthorColumnName) {
			this.sqlCommitAuthorColumnName = sqlCommitAuthorColumnName;
		}

		public String getSqlCommitCommitDateColumnName() {
			return sqlCommitCommitDateColumnName;
		}

		public void setSqlCommitCommitDateColumnName(String sqlCommitCommitDateColumnName) {
			this.sqlCommitCommitDateColumnName = sqlCommitCommitDateColumnName;
		}

		public String getSqlCommitCommitDateInstantColumnName() {
			return sqlCommitCommitDateInstantColumnName;
		}

		public void setSqlCommitCommitDateInstantColumnName(String sqlCommitCommitDateInstantColumnName) {
			this.sqlCommitCommitDateInstantColumnName = sqlCommitCommitDateInstantColumnName;
		}

		public String getSqlCommitCommitIdColumName() {
			return sqlCommitCommitIdColumName;
		}

		public void setSqlCommitCommitIdColumName(String sqlCommitCommitIdColumName) {
			this.sqlCommitCommitIdColumName = sqlCommitCommitIdColumName;
		}

		public String getSqlCommitPropertyCommitFKColumnName() {
			return sqlCommitPropertyCommitFKColumnName;
		}

		public void setSqlCommitPropertyCommitFKColumnName(String sqlCommitPropertyCommitFKColumnName) {
			this.sqlCommitPropertyCommitFKColumnName = sqlCommitPropertyCommitFKColumnName;
		}

		public String getSqlCommitPropertyNameColumnName() {
			return sqlCommitPropertyNameColumnName;
		}

		public void setSqlCommitPropertyNameColumnName(String sqlCommitPropertyNameColumnName) {
			this.sqlCommitPropertyNameColumnName = sqlCommitPropertyNameColumnName;
		}

		public String getSqlCommitPropertyValueColumnName() {
			return sqlCommitPropertyValueColumnName;
		}

		public void setSqlCommitPropertyValueColumnName(String sqlCommitPropertyValueColumnName) {
			this.sqlCommitPropertyValueColumnName = sqlCommitPropertyValueColumnName;
		}

		public String getSqlSnapshotPKColumnName() {
			return sqlSnapshotPKColumnName;
		}

		public void setSqlSnapshotPKColumnName(String sqlSnapshotPKColumnName) {
			this.sqlSnapshotPKColumnName = sqlSnapshotPKColumnName;
		}

		public String getSqlSnapshotCommitFKColumnName() {
			return sqlSnapshotCommitFKColumnName;
		}

		public void setSqlSnapshotCommitFKColumnName(String sqlSnapshotCommitFKColumnName) {
			this.sqlSnapshotCommitFKColumnName = sqlSnapshotCommitFKColumnName;
		}

		public String getSqlSnapshotGlobalIdFKColumnName() {
			return sqlSnapshotGlobalIdFKColumnName;
		}

		public void setSqlSnapshotGlobalIdFKColumnName(String sqlSnapshotGlobalIdFKColumnName) {
			this.sqlSnapshotGlobalIdFKColumnName = sqlSnapshotGlobalIdFKColumnName;
		}

		public String getSqlSnapshotTypeColumnName() {
			return sqlSnapshotTypeColumnName;
		}

		public void setSqlSnapshotTypeColumnName(String sqlSnapshotTypeColumnName) {
			this.sqlSnapshotTypeColumnName = sqlSnapshotTypeColumnName;
		}

		public String getSqlSnapshotVersionColumnName() {
			return sqlSnapshotVersionColumnName;
		}

		public void setSqlSnapshotVersionColumnName(String sqlSnapshotVersionColumnName) {
			this.sqlSnapshotVersionColumnName = sqlSnapshotVersionColumnName;
		}

		public String getSqlSnapshotStateColumnName() {
			return sqlSnapshotStateColumnName;
		}

		public void setSqlSnapshotStateColumnName(String sqlSnapshotStateColumnName) {
			this.sqlSnapshotStateColumnName = sqlSnapshotStateColumnName;
		}

		public String getSqlSnapshotChangedColumnName() {
			return sqlSnapshotChangedColumnName;
		}

		public void setSqlSnapshotChangedColumnName(String sqlSnapshotChangedColumnName) {
			this.sqlSnapshotChangedColumnName = sqlSnapshotChangedColumnName;
		}

		public String getSqlSnapshotManagedTypeColumnName() {
			return sqlSnapshotManagedTypeColumnName;
		}

		public void setSqlSnapshotManagedTypeColumnName(String sqlSnapshotManagedTypeColumnName) {
			this.sqlSnapshotManagedTypeColumnName = sqlSnapshotManagedTypeColumnName;
		}

		public String getSqlPrimaryKeyIndicator() {
			return sqlPrimaryKeyIndicator;
		}

		public void setSqlPrimaryKeyIndicator(String sqlPrimaryKeyIndicator) {
			this.sqlPrimaryKeyIndicator = sqlPrimaryKeyIndicator;
		}

		public String getSqlForeignKeyIndicator() {
			return sqlForeignKeyIndicator;
		}

		public void setSqlForeignKeyIndicator(String sqlForeignKeyIndicator) {
			this.sqlForeignKeyIndicator = sqlForeignKeyIndicator;
		}

		public String getSqlSequenceIndicator() {
			return sqlSequenceIndicator;
		}

		public void setSqlSequenceIndicator(String sqlSequenceIndicator) {
			this.sqlSequenceIndicator = sqlSequenceIndicator;
		}		
		
		public String getSqlIndexIndicator() {
			return sqlIndexIndicator;
		}

		public void setSqlIndexIndicator(String sqlIndexIndicator) {
			this.sqlIndexIndicator = sqlIndexIndicator;
		}				

		public boolean getSqlIsSuffix() {
			return sqlIsSuffix;
		}

		public void setSqlIsSuffix(boolean sqlIsSuffix) {
			this.sqlIsSuffix = sqlIsSuffix;
		}		
}
