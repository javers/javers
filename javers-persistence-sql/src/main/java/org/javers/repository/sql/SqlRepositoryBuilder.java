package org.javers.repository.sql;

import org.javers.core.AbstractContainerBuilder;
import org.javers.repository.sql.pico.JaversSqlModule;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.PolyJDBCBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author bartosz walacik
 */
public class SqlRepositoryBuilder extends AbstractContainerBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SqlRepositoryBuilder.class);

    private DialectName dialectName;
    private ConnectionProvider connectionProvider;

    private String schemaName;
    private boolean globalIdCacheDisabled;

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
        if (schemaName != null && !schemaName.isEmpty()) {
            this.schemaName = schemaName;
        }
        return this;
    }

    /**
     * See {@link JaversSqlRepository#evictCache()}
     *
     * @since 2.7.2
     */
    public SqlRepositoryBuilder withGlobalIdCacheDisabled() {
        globalIdCacheDisabled = true;
        return this;
    }

    public JaversSqlRepository build() {
        logger.info("starting up SQL repository module ...");
        bootContainer();

        SqlRepositoryConfiguration config =
                new SqlRepositoryConfiguration(globalIdCacheDisabled, schemaName);
        addComponent(config);

        PolyJDBC polyJDBC = PolyJDBCBuilder.polyJDBC(dialectName.getPolyDialect(), config.getSchemaName())
                .usingManagedConnections(new org.polyjdbc.core.transaction.ConnectionProvider() {
                    @Override
                    public Connection getConnection() throws SQLException {
                        return connectionProvider.getConnection();
                    }
                }).build();

        addComponent(polyJDBC);
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
