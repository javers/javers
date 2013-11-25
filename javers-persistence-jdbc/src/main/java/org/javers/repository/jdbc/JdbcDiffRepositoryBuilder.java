package org.javers.repository.jdbc;

import org.javers.common.validation.Validate;
import org.javers.core.AbstractJaversBuilder;
import org.javers.repository.jdbc.pico.JdbcJaversModule;
import org.javers.repository.jdbc.schema.JaversSchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * Supports two configuring methods:
 * <ul>
 *     <li/>by properties file, see {@link #configure(String)}
 *     <li/>programmatically using builder style methods
 * </ul>
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

    /**
     * loads a properties file from classpath, example file:
     * <pre>
     *   jdbc.dialect =POSTGRES
     *   jdbc.database.url =jdbc:postgresql://localhost/javers_db
     *   jdbc.database.username =javers
     *   jdbc.database.password =javers
     * </pre>
     * @param classpathName classpath resource name, ex. "configuration/jdbc-postgres.properties",
     *                      see {@link ClassLoader#getResourceAsStream(String)}
     */
    public JdbcDiffRepositoryBuilder configure(String classpathName){
        jdbcConfiguration.readProperties(classpathName);
        return this;
    }

    public JdbcDiffRepositoryBuilder withDialect(DialectName dialect) {
        jdbcConfiguration.withDialect(dialect);
        return this;
    }

    public JdbcDiffRepositoryBuilder withDatabaseUrl(String databaseUrl) {
        jdbcConfiguration.withDatabaseUrl(databaseUrl);
        return this;
    }

    public JdbcDiffRepositoryBuilder withUsername(String username) {
        jdbcConfiguration.withUsername(username);
        return this;
    }

    public JdbcDiffRepositoryBuilder withPassword(String password) {
        jdbcConfiguration.withPassword(password);
        return this;
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
