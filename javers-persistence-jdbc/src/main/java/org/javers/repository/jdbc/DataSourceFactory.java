package org.javers.repository.jdbc;

import org.javers.common.validation.Validate;
import org.apache.commons.dbcp.BasicDataSource;
import org.javers.repository.jdbc.schema.JaversSchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * @author bartosz walacik
 */
public class DataSourceFactory {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);

    public static DataSource createDBCP(String driverClassName, String url, String username, String password) {
        Validate.argumentIsNotNull(driverClassName);
        Validate.argumentIsNotNull(url);

        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDefaultAutoCommit(false);

        logger.info("creating ConnectionPool for database "+url);

        return dataSource;

    }
}
