package org.javers.repository.sql.integration.docker

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

@Testcontainers
class MySqlDockerIntegrationWithNativeJsonSupportTest extends JaversSqlRepositoryE2ETest {

    @Shared
    public MySQLContainer postgres = new MySQLContainer()

    Connection createConnection() {
       String url = postgres.jdbcUrl
       String user = postgres.username
       String pass = postgres.password

       DriverManager.getConnection(url, user, pass)
    }

    DialectName getDialect() {
        DialectName.MYSQL
    }

    String getSchema() {
        return null
    }

    @Override
    boolean isUsingNativeJsonSupport() {
        true
    }
}
