package org.javers.repository.jdbc;

import org.javers.core.AbstractJaversBuilder;
import org.javers.repository.jdbc.pico.JdbcJaversModule;
import org.javers.repository.jdbc.schema.JaversSchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @author bartosz walacik
 */
public class JdbcDiffRepositoryBuilder extends AbstractJaversBuilder {
    private static final Logger logger = LoggerFactory.getLogger(JdbcDiffRepositoryBuilder.class);

    private JdbcRepositoryConfiguration jdbcConfiguration;

    private JdbcDiffRepositoryBuilder() {
        jdbcConfiguration = new JdbcRepositoryConfiguration();
    }

    public static JdbcDiffRepositoryBuilder jdbcDiffRepository() {
        return new JdbcDiffRepositoryBuilder();
    }

    public JdbcDiffRepository build() {
        logger.info("starting up JDBC repository module ...");
        bootContainer(new JdbcJaversModule(),
                      Arrays.asList(jdbcConfiguration.createConnectionPool(),
                                    jdbcConfiguration.getPollyDialect()));

        createSchemaIfNotExists();

        return getContainerComponent(JdbcDiffRepository.class);
    }

    private void createSchemaIfNotExists() {
        JaversSchemaManager schemaManager = getContainerComponent(JaversSchemaManager.class);
        schemaManager.createSchemaIfNotExists();
    }

    /**
     * For testing only
     */
    @Override
    protected <T> T getContainerComponent(Class<T> ofClass) {
        return super.getContainerComponent(ofClass);
    }
}
