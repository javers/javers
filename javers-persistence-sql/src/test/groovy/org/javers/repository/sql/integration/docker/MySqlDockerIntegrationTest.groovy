package org.javers.repository.sql.integration.docker

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest
import org.junit.ClassRule
import org.testcontainers.containers.MySQLContainer
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

class MySqlDockerIntegrationTest extends JaversSqlRepositoryE2ETest {

    @ClassRule @Shared
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
}
