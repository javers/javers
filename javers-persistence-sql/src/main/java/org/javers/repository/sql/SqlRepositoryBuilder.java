package org.javers.repository.sql;

import org.javers.core.AbstractJaversBuilder;
import org.javers.core.json.JsonConverter;
import org.javers.repository.sql.infrastructure.pico.JaversSqlModule;
import org.javers.repository.sql.domain.JaversSchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bartosz walacik
 */
public class SqlRepositoryBuilder extends AbstractJaversBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SqlRepositoryBuilder.class);

    private DialectName dialectName;
    private ConnectionProvider connectionProvider;
    private JsonConverter jsonConverter;

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

    public JaversSqlRepository build() {
        logger.info("starting up SQL repository module ...");
        bootContainer();
        addModule(new JaversSqlModule());
        addComponent(dialectName.getPolyDialect());
        addComponent(dialectName);
        addComponent(connectionProvider);
        addComponent(jsonConverter);

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

    public SqlRepositoryBuilder withJSONConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
        return this;
    }
}
