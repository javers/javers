package org.javers.repository.sql.schema;

import java.util.Optional;
import org.javers.repository.sql.SqlRepositoryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ian Agius
 */
public class DBNameProvider {
    /*private static final String SNAPSHOT_TABLE_PK_SEQ = "jv_snapshot_pk_seq";
    private static final String COMMIT_PK_SEQ =        "jv_commit_pk_seq";
    private static final String GLOBAL_ID_PK_SEQ =     "jv_global_id_pk_seq";*/


    private static final String DEFAULT_GLOBAL_ID_TABLE_NAME = "jv_global_id";
    private static final String DEFAULT_SNAPSHOT_TABLE_NAME =   "jv_snapshot";
    private static final String DEFAULT_COMMIT_TABLE_NAME =    "jv_commit";
    private static final String DEFAULT_COMMIT_PROPERTY_TABLE_NAME = "jv_commit_property";

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
    public static final String SNAPSHOT_MANAGED_TYPE = "managed_type";       //since 2.0
    
    private static final Logger logger = LoggerFactory.getLogger(DBNameProvider.class);
    private final SqlRepositoryConfiguration configuration;

    public DBNameProvider(SqlRepositoryConfiguration configuration) {
        this.configuration = configuration;
        // Table Names
        logger.info("Commit table:               {}", getCommitTableNameWithSchema());
        logger.info("CommitProperty table:       {}", getCommitPropertyTableNameWithSchema());
        logger.info("GlobalId table:             {}", getGlobalIdTableNameWithSchema());
        logger.info("Snapshot table:             {}", getSnapshotTableNameWithSchema());
        // Column Names
		logger.debug("GLOBAL_ID_PK:               {}", getGlobalIdPKColumnName());
		logger.debug("GLOBAL_ID_LOCAL_ID:         {}", getGlobalIdLocalIdColumnName());
		logger.debug("GLOBAL_ID_FRAGMENT:         {}", getGlobalIdFragmentColumnName());
		logger.debug("GLOBAL_ID_TYPE_NAME:        {}", getGlobalIdTypeNameColumnName());
		logger.debug("GLOBAL_ID_OWNER_ID_FK:      {}", getGlobalIdOwnerIDFKColumnName());
		logger.debug("COMMIT_PK:                  {}", getCommitPKColumnName());
		logger.debug("COMMIT_AUTHOR:              {}", getCommitAuthorColumnName());
		logger.debug("COMMIT_COMMIT_DATE:         {}", getCommitCommitDateColumnName());
		logger.debug("COMMIT_COMMIT_DATE_INSTANT: {}", getCommitCommitDateInstantColumnName());
		logger.debug("COMMIT_COMMIT_ID:           {}", getCommitCommitIdColumName());
		logger.debug("COMMIT_PROPERTY_COMMIT_FK:  {}", getCommitPropertyCommitFKColumnName());
		logger.debug("COMMIT_PROPERTY_NAME:       {}", getCommitPropertyNameColumnName());
		logger.debug("COMMIT_PROPERTY_VALUE:      {}", getCommitPropertyValueColumnName());
		logger.debug("SNAPSHOT_PK:                {}", getSnapshotPKColumnName());
		logger.debug("SNAPSHOT_COMMIT_FK:         {}", getSnapshotCommitFKColumnName());
		logger.debug("SNAPSHOT_GLOBAL_ID_FK:      {}", getSnapshotGlobalIdFKColumnName());
		logger.debug("SNAPSHOT_TYPE:              {}", getSnapshotTypeColumnName());
		logger.debug("SNAPSHOT_VERSION:           {}", getSnapshotVersionColumnName());
		logger.debug("SNAPSHOT_STATE:             {}", getSnapshotStateColumnName());
		logger.debug("SNAPSHOT_CHANGED:           {}", getSnapshotChangedColumnName());
		logger.debug("SNAPSHOT_MANAGED_TYPE:      {}", getSnapshotManagedTypeColumnName());

    }

    public String getGlobalIdTableNameWithSchema() {
        return getGlobalIdTableName().nameWithSchema();
    }

    public String getCommitTableNameWithSchema() {
        return getCommitTableName().nameWithSchema();
    }

    public String getCommitPropertyTableNameWithSchema() {
        return getCommitPropertyTableName().nameWithSchema();
    }

    public String getSnapshotTableNameWithSchema() {
        return getSnapshotTableName().nameWithSchema();
    }

    public DBObjectName getSnapshotTablePkSeqName() {
        return fullDbName("SQ_"+getSnapshotPKColumnName());
    }

    public DBObjectName getGlobalIdPkSeqName() {
        return fullDbName("SQ_"+getGlobalIdPKColumnName());
    }

    public DBObjectName getCommitPkSeqName() {
        return fullDbName("SQ_"+getCommitPKColumnName());
    }

    /**
     * used only by migration scripts
     */
    @Deprecated
    public String getCdoClassTableNameWithSchema() {
        return fullDbName("jv_cdo_class").nameWithSchema();
    }

    DBObjectName getGlobalIdTableName() {
        return fullDbName(configuration.getGlobalIdTableName().orElse(DEFAULT_GLOBAL_ID_TABLE_NAME));
    }

    DBObjectName getCommitTableName() {
        return fullDbName(configuration.getCommitTableName().orElse(DEFAULT_COMMIT_TABLE_NAME));
    }

    DBObjectName getCommitPropertyTableName() {
        return fullDbName(configuration.getCommitPropertyTableName().orElse(DEFAULT_COMMIT_PROPERTY_TABLE_NAME));
    }

    DBObjectName getSnapshotTableName() {
        return fullDbName(configuration.getSnapshotTableName().orElse(DEFAULT_SNAPSHOT_TABLE_NAME));
    }

    Optional<String> getSchemaName() {
        return configuration.getSchemaNameAsOptional();
    }
    
    private DBObjectName fullDbName(String name) {
        return new DBObjectName(configuration.getSchemaNameAsOptional(), name);
    }
    
    public String getGlobalIdPKColumnName() {
        return configuration.getGlobalIdPKColumnName().orElse(GLOBAL_ID_PK);
    }
    
    public String getGlobalIdLocalIdColumnName() {
        return configuration.getGlobalIdLocalIdColumnName().orElse(GLOBAL_ID_LOCAL_ID);
    }

    public String getGlobalIdFragmentColumnName() {
        return configuration.getGlobalIdFragmentColumnName().orElse(GLOBAL_ID_FRAGMENT);
    }
    
    public String getGlobalIdTypeNameColumnName() {
        return configuration.getGlobalIdTypeNameColumnName().orElse(GLOBAL_ID_TYPE_NAME);
    }
    
    public String getGlobalIdOwnerIDFKColumnName() {
        return configuration.getGlobalIdOwnerIDFKColumnName().orElse(GLOBAL_ID_OWNER_ID_FK);
    }
    
    public String getCommitPKColumnName() {
        return configuration.getCommitPKColumnName().orElse(COMMIT_PK);
    }

    public String getCommitAuthorColumnName() {
        return configuration.getCommitAuthorColumnName().orElse(COMMIT_AUTHOR);
    }
    
    public String getCommitCommitDateColumnName() {
        return configuration.getCommitCommitDateColumnName().orElse(COMMIT_COMMIT_DATE);
    }
    
    public String getCommitCommitDateInstantColumnName() {
        return configuration.getCommitCommitDateInstantColumnName().orElse(COMMIT_COMMIT_DATE_INSTANT);
    }
    
    public String getCommitCommitIdColumName() {
        return configuration.getCommitCommitIdColumName().orElse(COMMIT_COMMIT_ID);
    }
    
    public String getCommitPropertyCommitFKColumnName() {
        return configuration.getCommitPropertyCommitFKColumnName().orElse(COMMIT_PROPERTY_COMMIT_FK);
    }
    
    public String getCommitPropertyNameColumnName() {
        return configuration.getCommitPropertyNameColumnName().orElse(COMMIT_PROPERTY_NAME);
    }

    public String getCommitPropertyValueColumnName() {
        return configuration.getCommitPropertyValueColumnName().orElse(COMMIT_PROPERTY_VALUE);
    }    
    
    public String getSnapshotPKColumnName() {
        return configuration.getSnapshotPKColumnName().orElse(SNAPSHOT_PK);
    }

    public String getSnapshotCommitFKColumnName() {
        return configuration.getSnapshotCommitFKColumnName().orElse(SNAPSHOT_COMMIT_FK);
    }
    
    public String getSnapshotGlobalIdFKColumnName() {
        return configuration.getSnapshotGlobalIdFKColumnName().orElse(SNAPSHOT_GLOBAL_ID_FK);
    }
    
    public String getSnapshotTypeColumnName() {
        return configuration.getSnapshotTypeColumnName().orElse(SNAPSHOT_TYPE);
    }
    
    public String getSnapshotVersionColumnName() {
        return configuration.getSnapshotVersionColumnName().orElse(SNAPSHOT_VERSION);
    }

    public String getSnapshotStateColumnName() {
        return configuration.getSnapshotStateColumnName().orElse(SNAPSHOT_STATE);
    }
    
    public String getSnapshotChangedColumnName() {
        return configuration.getSnapshotChangedColumnName().orElse(SNAPSHOT_CHANGED);
    }
    
    public String getSnapshotManagedTypeColumnName() {
        return configuration.getSnapshotManagedTypeColumnName().orElse(SNAPSHOT_MANAGED_TYPE);
    }
}
