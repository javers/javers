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

    public static String SCHEMA_NAME = "";
    public static final String SCHEMA_TABLE_SEP = ".";

    private DialectName dialectName;
    private ConnectionProvider connectionProvider;

    public SqlRepositoryBuilder() {
    }

    public static SqlRepositoryBuilder sqlRepository() {
        return new SqlRepositoryBuilder();
    }

    public SqlRepositoryBuilder withDialect(DialectName dialect) {
        dialectName = dialect;
        return this;
    }

    public SqlRepositoryBuilder withConnectionProvider(ConnectionProvider connectionProvider){
        this.connectionProvider = connectionProvider;
        return this;
    }

    /**
     * This function sets a schema to be used for creation and updating tables. When passing a schema name make sure
     * that the schema has been created in the database before running JaVers.
     *
     * Example: CREATE SCHEMA my_schema_name AUTHORIZATION my_db;
     *
     * @param schemaName
     * @return
     */
    public SqlRepositoryBuilder withSchema(String schemaName){
        SCHEMA_NAME = schemaName;
        return this;
    }

    public JaversSqlRepository build() {
        logger.info("starting up SQL repository module ...");
        bootContainer();

        PolyJDBC polyJDBC = PolyJDBCBuilder.polyJDBC(dialectName.getPolyDialect())
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
