package org.javers.repository.sql.schema;

import org.javers.repository.sql.SqlRepositoryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnNameProvider {

	public static final String GLOBAL_ID_PK =         "global_id_pk";
    public static final String GLOBAL_ID_LOCAL_ID =   "local_id";
    public static final String GLOBAL_ID_FRAGMENT =   "fragment";     //since 1.2
    public static final String GLOBAL_ID_TYPE_NAME =  "type_name";    //since 2.0
    public static final String GLOBAL_ID_OWNER_ID_FK ="owner_id_fk";  //since 1.2

    public static final String COMMIT_PK =            "commit_pk";
    public static final String COMMIT_AUTHOR =        "author";
    public static final String COMMIT_COMMIT_DATE =   "commit_date";
    public static final String COMMIT_COMMIT_DATE_INSTANT =   "commit_date_instant";
    public static final String COMMIT_COMMIT_ID =     "commit_id";
    public static final String COMMIT_PROPERTY_COMMIT_FK =  "commit_fk";
    public static final String COMMIT_PROPERTY_NAME =       "property_name";
    public static final String COMMIT_PROPERTY_VALUE =      "property_value";

    public static final String SNAPSHOT_PK =           "snapshot_pk";
    public static final String SNAPSHOT_COMMIT_FK =    "commit_fk";
    public static final String SNAPSHOT_GLOBAL_ID_FK = "global_id_fk";
    public static final String SNAPSHOT_TYPE =         "type";
    public static final String SNAPSHOT_VERSION =      "version";
    public static final String SNAPSHOT_STATE =        "state";
    public static final String SNAPSHOT_CHANGED =      "changed_properties"; //since v 1.2
    public static final String SNAPSHOT_MANAGED_TYPE = "managed_type";       //since 2.0*/
    
    private static final Logger logger = LoggerFactory.getLogger(TableNameProvider.class);
    private final SqlRepositoryConfiguration configuration;

    public ColumnNameProvider(SqlRepositoryConfiguration configuration) {
        this.configuration = configuration;
		logger.info("GLOBAL_ID_PK:               {}", getGlobalIdPKName());
		logger.info("GLOBAL_ID_LOCAL_ID:         {}", getGlobalIdLocalIdName());
		logger.info("GLOBAL_ID_FRAGMENT:         {}", getGlobalIdFragmentName());
		logger.info("GLOBAL_ID_TYPE_NAME:        {}", getGlobalIdTypeName());
		logger.info("GLOBAL_ID_OWNER_ID_FK:      {}", getGlobalIdOwnerIDFKName());
		logger.info("COMMIT_PK:                  {}", getCommitPKName());
		logger.info("COMMIT_AUTHOR:              {}", getCommitAuthorName());
		logger.info("COMMIT_COMMIT_DATE:         {}", getCommitDateName());
		logger.info("COMMIT_COMMIT_DATE_INSTANT: {}", getCommitInstantName());
		logger.info("COMMIT_COMMIT_ID:           {}", getCommitIdName());
		logger.info("COMMIT_PROPERTY_COMMIT_FK:  {}", getCommitPropertyCommitFKName());
		logger.info("COMMIT_PROPERTY_NAME:       {}", getCommitPropertyName());
		logger.info("COMMIT_PROPERTY_VALUE:      {}", getCommitPropertyValueName());
		logger.info("SNAPSHOT_PK:                {}", getSnapshotPKName());
		logger.info("SNAPSHOT_COMMIT_FK:         {}", getSnapshotCommitFKName());
		logger.info("SNAPSHOT_GLOBAL_ID_FK:      {}", getSnapshotGlobalIDName());
		logger.info("SNAPSHOT_TYPE:              {}", getSnapshotTypeName());
		logger.info("SNAPSHOT_VERSION:           {}", getSnapshotVersionName());
		logger.info("SNAPSHOT_STATE:             {}", getSnapshotStateName());
		logger.info("SNAPSHOT_CHANGED:           {}", getSnapshotChangedName());
		logger.info("SNAPSHOT_MANAGED_TYPE:      {}", getSnapshotManagedTypeName());
    }

    
    public String getGlobalIdPKName() {
    	return this.configuration.getGlobalIdPKColumnName().orElse(GLOBAL_ID_PK);
    }
    
    public String getGlobalIdLocalIdName() {
    	return this.configuration.getGlobalIdLocalIdColumnName().orElse(GLOBAL_ID_LOCAL_ID);
    }
    
    public String getGlobalIdFragmentName() {
    	return this.configuration.getGlobalIdFragmentColumnName().orElse(GLOBAL_ID_FRAGMENT);
    }
    
    public String getGlobalIdTypeName() {
    	return this.configuration.getGlobalIdTypeNameColumnName().orElse(GLOBAL_ID_TYPE_NAME);
    }
    
    public String getGlobalIdOwnerIDFKName() {
    	return this.configuration.getGlobalIdOwnerIdFKColumnName().orElse(GLOBAL_ID_OWNER_ID_FK);
    }
    
    public String getCommitPKName() {
    	return this.configuration.getCommitIdPKColumnName().orElse(COMMIT_PK);
    }
    
    public String getCommitAuthorName() {
    	return this.configuration.getCommitAuthorColumnName().orElse(COMMIT_AUTHOR);
    }
    
    public String getCommitDateName() {
    	return this.configuration.getCommitDateColumnName().orElse(COMMIT_COMMIT_DATE);
    }
    
    public String getCommitInstantName() {
    	return this.configuration.getCommitInstantColumnName().orElse(COMMIT_COMMIT_DATE_INSTANT);
    }
    
    public String getCommitIdName() {
    	return this.configuration.getCommitIdColumnName().orElse(COMMIT_COMMIT_ID);
    }
    
    public String getCommitPropertyCommitFKName() {
    	return this.configuration.getCommitPropertyFKColumnName().orElse(COMMIT_PROPERTY_COMMIT_FK);
    }
    
    public String getCommitPropertyName() {
    	return this.configuration.getCommitPropertyNameColumnName().orElse(COMMIT_PROPERTY_NAME);
    }

    public String getCommitPropertyValueName() {
    	return this.configuration.getCommitPropertyValueColumnName().orElse(COMMIT_PROPERTY_VALUE);
    }
    
    public String getSnapshotPKName() {
    	return this.configuration.getSnapshotPKColumnName().orElse(SNAPSHOT_PK);
    }
    
    public String getSnapshotCommitFKName() {
    	return this.configuration.getSnapshotCommitFKColumnName().orElse(SNAPSHOT_COMMIT_FK);
    }
    
    public String getSnapshotGlobalIDName() {
    	return this.configuration.getSnapshotGlobalIDColumnName().orElse(SNAPSHOT_GLOBAL_ID_FK);
    }
    
    public String getSnapshotTypeName() {
    	return this.configuration.getSnapshotTypeColumnName().orElse(SNAPSHOT_TYPE);
    }

    public String getSnapshotVersionName() {
    	return this.configuration.getSnapshotVersionColumnName().orElse(SNAPSHOT_VERSION);
    }
    
    public String getSnapshotStateName() {
    	return this.configuration.getSnapshotStateColumnName().orElse(SNAPSHOT_STATE);
    }
    
    public String getSnapshotChangedName() {
    	return this.configuration.getSnapshotChangedColumnName().orElse(SNAPSHOT_CHANGED);
    }
    
    public String getSnapshotManagedTypeName() {
    	return this.configuration.getSnapshotManagedTypeColumnName().orElse(SNAPSHOT_MANAGED_TYPE);
    }

}
