package org.javers.repository.sql;

import static org.javers.common.string.Strings.isNonEmpty;

import org.javers.core.AbstractContainerBuilder;
import org.javers.repository.sql.pico.JaversSqlModule;
import org.javers.repository.sql.session.SessionFactory;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.PolyJDBCBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bartosz walacik
 */
public class SqlRepositoryBuilder extends AbstractContainerBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SqlRepositoryBuilder.class);

    private DialectName dialectName;
    private ConnectionProvider connectionProvider;
    
    private boolean globalIdCacheDisabled;
    private String schemaName;
    private boolean schemaManagementEnabled = true;
    
    private String globalIdTableName;
    private String globalIdPKColumnName;
    private String globalIdLocalIdColumnName;
    private String globalIdFragmentColumnName;
    private String globalIdTypeNameColumnName;
    private String globalIdOwnerIdFKColumnName;

    private String commitTableName;
    private String commitIdPKColumnName;
    private String commitAuthorColumnName;
    private String commitDateColumnName;
    private String commitInstantColumnName;
    private String commitIdColumnName;
    
    
    private String commitPropertyTableName;
    private String commitPropertyFKColumnName;
    private String commitPropertyNameColumnName;
    private String commitPropertyValueColumnName;
    
    private String snapshotTableName;
    private String snapshotPKColumnName;
    private String snapshotCommitFKColumnName;
    private String snapshotGlobalIDColumnName;
    private String snapshotTypeColumnName;
    private String snapshotVersionColumnName;
    private String snapshotStateColumnName;
    private String snapshotChangedColumnName;
    private String snapshotManagedTypeColumnName;


    public SqlRepositoryBuilder() {
    }

    public static SqlRepositoryBuilder sqlRepository() {
        return new SqlRepositoryBuilder();
    }

    public SqlRepositoryBuilder withDialect(DialectName dialect) {
        dialectName = dialect;
        return this;
    }

    public SqlRepositoryBuilder withConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        return this;
    }

    /**
     * This function sets a schema to be used for creation and updating tables. When passing a schema name make sure
     * that the schema has been created in the database before running JaVers. If schemaName is null or empty, the default
     * schema is used instead.
     *
     * @since 2.4
     */
    public SqlRepositoryBuilder withSchema(String schemaName) {
        if (isNonEmpty(schemaName)) {
            this.schemaName = schemaName;
        }
        return this;
    }

    /**
     * Since 2.7.2, JaversTransactionalDecorator evicts the cache on transaction rollback,
     * so there are no known reasons to disabling it.
     */
    public SqlRepositoryBuilder withGlobalIdCacheDisabled(boolean globalIdCacheDisabled) {
        this.globalIdCacheDisabled = globalIdCacheDisabled;
        return this;
    }

    public SqlRepositoryBuilder withSchemaManagementEnabled(boolean schemaManagementEnabled){
        this.schemaManagementEnabled = schemaManagementEnabled;
        return this;
    }

    public SqlRepositoryBuilder withGlobalIdTableName(String globalIdTableName) {
        if(isNonEmpty(globalIdTableName)) {
            this.globalIdTableName = globalIdTableName;
        }
        return this;
    }

    public SqlRepositoryBuilder withCommitTableName(String commitTableName) {
        if(isNonEmpty(commitTableName)) {
            this.commitTableName = commitTableName;
        }
        return this;
    }

    public SqlRepositoryBuilder withSnapshotTableName(String snapshotTableName) {
        if(isNonEmpty(snapshotTableName)) {
            this.snapshotTableName = snapshotTableName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withCommitPropertyTableName(String commitPropertyTableName) {
    	if(isNonEmpty(commitPropertyTableName)) {
            this.commitPropertyTableName = commitPropertyTableName;
        }
        return this;
    }

    public SqlRepositoryBuilder withGlobalIdPKColumnName(String globalIdPKColumnName) {
        if(isNonEmpty(globalIdPKColumnName)) {
            this.globalIdPKColumnName = globalIdPKColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withGlobalIdLocalIdColumnName(String globalIdLocalIdColumnName) {
        if(isNonEmpty(globalIdLocalIdColumnName)) {
            this.globalIdLocalIdColumnName = globalIdLocalIdColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withGlobalIdFragmentColumnName(String globalIdFragmentColumnName) {
        if(isNonEmpty(globalIdFragmentColumnName)) {
            this.globalIdFragmentColumnName = globalIdFragmentColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withGlobalIdTypeNameColumnName(String globalIdTypeNameColumnName) {
        if(isNonEmpty(globalIdTypeNameColumnName)) {
            this.globalIdTypeNameColumnName = globalIdTypeNameColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withGlobalIdOwnerIdFKColumnName(String globalIdOwnerIdFKColumnName) {
        if(isNonEmpty(globalIdOwnerIdFKColumnName)) {
            this.globalIdOwnerIdFKColumnName = globalIdOwnerIdFKColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withCommitIdPKColumnName(String commitIdPKColumnName) {
        if(isNonEmpty(commitIdPKColumnName)) {
            this.commitIdPKColumnName = commitIdPKColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withCommitAuthorColumnName(String commitAuthorColumnName) {
        if(isNonEmpty(commitAuthorColumnName)) {
            this.commitAuthorColumnName = commitAuthorColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withCommitDateColumnName(String commitDateColumnName) {
        if(isNonEmpty(commitDateColumnName)) {
            this.commitDateColumnName = commitDateColumnName;
        }
        return this;
    }

    public SqlRepositoryBuilder withCommitInstantColumnName(String commitInstantColumnName) {
        if(isNonEmpty(commitInstantColumnName)) {
            this.commitInstantColumnName = commitInstantColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withCommitIdColumnName(String commitIdColumnName) {
        if(isNonEmpty(commitIdColumnName)) {
            this.commitIdColumnName = commitIdColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withCommitPropertyFKColumnName(String commitPropertyFKColumnName) {
        if(isNonEmpty(commitPropertyFKColumnName)) {
            this.commitPropertyFKColumnName = commitPropertyFKColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withCommitPropertyNameColumnName(String commitPropertyNameColumnName) {
        if(isNonEmpty(commitPropertyNameColumnName)) {
            this.commitPropertyNameColumnName = commitPropertyNameColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withCommitPropertyValueColumnName(String commitPropertyValueColumnName) {
        if(isNonEmpty(commitPropertyValueColumnName)) {
            this.commitPropertyValueColumnName = commitPropertyValueColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withSnapshotPKColumnName(String snapshotPKColumnName) {
        if(isNonEmpty(snapshotPKColumnName)) {
            this.snapshotPKColumnName = snapshotPKColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withSnapshotCommitFKColumnName(String snapshotCommitFKColumnName) {
        if(isNonEmpty(snapshotCommitFKColumnName)) {
            this.snapshotCommitFKColumnName = snapshotCommitFKColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withSnapshotGlobalIDColumnName(String snapshotGlobalIDColumnName) {
        if(isNonEmpty(snapshotGlobalIDColumnName)) {
            this.snapshotGlobalIDColumnName = snapshotGlobalIDColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withSnapshotTypeColumnName(String snapshotTypeColumnName) {
        if(isNonEmpty(snapshotTypeColumnName)) {
            this.snapshotTypeColumnName = snapshotTypeColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withSnapshotVersionColumnName(String snapshotVersionColumnName) {
        if(isNonEmpty(snapshotVersionColumnName)) {
            this.snapshotVersionColumnName = snapshotVersionColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withSnapshotStateColumnName(String snapshotStateColumnName) {
        if(isNonEmpty(snapshotStateColumnName)) {
            this.snapshotStateColumnName = snapshotStateColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withSnapshotChangedColumnName(String snapshotChangedColumnName) {
        if(isNonEmpty(snapshotStateColumnName)) {
            this.snapshotChangedColumnName = snapshotChangedColumnName;
        }
        return this;
    }
    
    public SqlRepositoryBuilder withSnapshotManagedTypeColumnName(String snapshotManagedTypeColumnName) {
        if(isNonEmpty(snapshotManagedTypeColumnName)) {
            this.snapshotManagedTypeColumnName = snapshotManagedTypeColumnName;
        }
        return this;
    }
   
    public JaversSqlRepository build() {
        logger.info("starting SqlRepository...");
        logger.info("  dialect:                  {}", dialectName);
        logger.info("  schemaManagementEnabled:  {}", schemaManagementEnabled);
        logger.info("  schema name:              {}", schemaName);
        bootContainer();

        SqlRepositoryConfiguration config =
                new SqlRepositoryConfiguration(globalIdCacheDisabled,
							                   schemaName,
							                   schemaManagementEnabled,
							                   globalIdTableName,
							                   commitTableName,
							                   snapshotTableName,
							                   commitPropertyTableName,
							                   globalIdPKColumnName,
							                   globalIdLocalIdColumnName,
							                   globalIdFragmentColumnName,
							                   globalIdTypeNameColumnName,
							                   globalIdOwnerIdFKColumnName,
							                   commitIdPKColumnName,
							                   commitAuthorColumnName,
							                   commitDateColumnName,
							                   commitInstantColumnName,
							                   commitIdColumnName,
							                   commitPropertyFKColumnName,
							                   commitPropertyNameColumnName,
							                   commitPropertyValueColumnName,
							                   snapshotPKColumnName,
							                   snapshotCommitFKColumnName,
							                   snapshotGlobalIDColumnName,
							                   snapshotTypeColumnName,
							                   snapshotVersionColumnName,
							                   snapshotStateColumnName,
							                   snapshotChangedColumnName,
							                   snapshotManagedTypeColumnName);
        addComponent(config);

        PolyJDBC polyJDBC = PolyJDBCBuilder.polyJDBC(dialectName.getPolyDialect(), config.getSchemaName())
                .usingManagedConnections(() -> connectionProvider.getConnection()).build();

        SessionFactory sessionFactory = new SessionFactory(dialectName, connectionProvider);

        addComponent(polyJDBC);
        addComponent(sessionFactory);

        addModule(new JaversSqlModule());

        addComponent(dialectName.getPolyDialect());
        addComponent(connectionProvider);
        return getContainerComponent(JaversSqlRepository.class);
    }

    /**
     * For testing only
     */
    @Override
    protected <T> T getContainerComponent(Class<T> ofClass) {
        return super.getContainerComponent(ofClass);
    }
}
