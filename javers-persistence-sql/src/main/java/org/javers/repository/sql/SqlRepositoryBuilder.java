package org.javers.repository.sql;

import org.javers.core.AbstractJaversBuilder;
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
public class SqlRepositoryBuilder extends AbstractJaversBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SqlRepositoryBuilder.class);

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
