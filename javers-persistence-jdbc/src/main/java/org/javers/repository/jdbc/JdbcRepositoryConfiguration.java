package org.javers.repository.jdbc;

import org.javers.common.validation.Validate;
import org.javers.core.PropertyConfiguration;
import org.polyjdbc.core.dialect.Dialect;

import javax.sql.DataSource;

import static org.javers.repository.jdbc.DataSourceFactory.*;

/**
 * @author bartosz walacik
 */
public class JdbcRepositoryConfiguration {
    public static final String DIALECT_PROPERTY =   "jdbc.dialect";
    public static final String URL_PROPERTY =       "jdbc.database.url";
    public static final String USERNAME_PROPERTY =  "jdbc.database.username";
    public static final String PASSWORD_PROPERTY =  "jdbc.database.password";

    private PropertyConfiguration propertyConfiguration;

    private DialectName dialectName;
    private String databaseUrl;
    private String username;
    private String password;

    public JdbcRepositoryConfiguration() {
        propertyConfiguration = new PropertyConfiguration("jdbc-default.properties");
        assemble();
    }

    public JdbcRepositoryConfiguration withDialect(DialectName dialect) {
        Validate.argumentIsNotNull(dialect);
        this.dialectName = dialect;
        return this;
    }

    public JdbcRepositoryConfiguration withDatabaseUrl(String databaseUrl) {
        Validate.argumentIsNotNull(databaseUrl);
        this.databaseUrl = databaseUrl;
        return this;
    }

    public JdbcRepositoryConfiguration withUsername(String username) {
        this.username = username;
        return this;
    }

    public JdbcRepositoryConfiguration withPassword(String password) {
        this.password = password;
        return this;
    }

    //-- getters

    /**
     * Sql dialect, mandatory
     */
    public DialectName getDialectName() {
        return dialectName;
    }

    /**
     * JDBC connection url, mandatory
     */
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    /**
     * Database username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Database password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return never returns null
     */
    public Dialect getPollyDialect() {
        return dialectName.getPolyDialect();
    }

    protected DataSource createConnectionPool() {
        return  createDBCP(getDialectName().getDriverClass(),
                           getDatabaseUrl(),
                           getUsername(),
                           getPassword());
    }

    public void assemble() {
        if (propertyConfiguration.contains(DIALECT_PROPERTY)) {
            dialectName = propertyConfiguration.getEnumProperty(DIALECT_PROPERTY, DialectName.class);
        }
        if (propertyConfiguration.contains(URL_PROPERTY))  {
            databaseUrl = propertyConfiguration.getStringProperty(URL_PROPERTY);
        }
        if (propertyConfiguration.contains(USERNAME_PROPERTY))  {
            username = propertyConfiguration.getStringProperty(USERNAME_PROPERTY);
        }
        if (propertyConfiguration.contains(PASSWORD_PROPERTY))  {
            password = propertyConfiguration.getStringProperty(PASSWORD_PROPERTY);
        }
    }


}
