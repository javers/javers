package org.javers.repository.sql;

import org.javers.core.AbstractJaversBuilder;
import org.javers.repository.sql.pico.JaversSqlModule;
import org.javers.repository.sql.schema.JaversSchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bartosz walacik
 */
public class SqlRepositoryBuilder extends AbstractJaversBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SqlRepositoryBuilder.class);

    private DialectName dialectName;
    private ConnectionProvider connectionProvider;

    private SqlRepositoryBuilder() {
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

    /*
    public SqlRepositoryBuilder withDatabaseUrl(String databaseUrl) {
        jdbcConfiguration.withDatabaseUrl(databaseUrl);
        return this;
    }
    public SqlRepositoryBuilder withUsername(String username) {
        jdbcConfiguration.withUsername(username);
        return this;
    }
    public SqlRepositoryBuilder withPassword(String password) {
        jdbcConfiguration.withPassword(password);
        return this;
    }*/

    public JaversSqlRepository build() {
        logger.info("starting up SQL repository module ...");
        bootContainer();
        addModule(new JaversSqlModule());
        addComponent(dialectName.getPolyDialect());
        addComponent(dialectName);
        addComponent(connectionProvider);

        ensureSchema();
        return getContainerComponent(JaversSqlRepository.class);
    }
    private void ensureSchema() {
        JaversSchemaManager schemaManager = getContainerComponent(JaversSchemaManager.class);
        schemaManager.ensureSchema();
    }

    /**
     * For testing only
     */
    @Override
    protected <T> T getContainerComponent(Class<T> ofClass) {
        return super.getContainerComponent(ofClass);
    }
}
