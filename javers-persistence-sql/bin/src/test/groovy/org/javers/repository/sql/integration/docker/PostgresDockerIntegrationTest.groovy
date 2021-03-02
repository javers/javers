package org.javers.repository.sql.integration.docker

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

import static org.testcontainers.containers.PostgreSQLContainer.IMAGE

@Testcontainers
class PostgresDockerIntegrationTest extends JaversSqlRepositoryE2ETest {

    @Shared
    public PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse(IMAGE).withTag("12.1")
    )

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
        return null
    }
}
