package org.javers.repository.sql.integration.docker

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest
import org.junit.ClassRule
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

class PostgresDockerIntegrationWithSchemaTest extends JaversSqlRepositoryE2ETest {

    @ClassRule @Shared
    public PostgreSQLContainer postgres = new PostgreSQLContainer()
            .withInitScript("init_postgresql_with_schema.sql")

    Connection createConnection() {
       String url = postgres.jdbcUrl
       String user = postgres.username
       String pass = postgres.password

       DriverManager.getConnection(url, user, pass)
    }

    DialectName getDialect() {
        DialectName.POSTGRES
    }

    String getSchema() {
        return "javers"
    }
}
