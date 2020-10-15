package org.javers.repository.sql.integration.docker

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest
import org.testcontainers.containers.OracleContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

@Testcontainers
class OracleDockerIntegrationWithSchemaTest extends JaversSqlRepositoryE2ETest {

    //docker image built by https://github.com/wnameless/docker-oracle-xe-11g.git
    @Shared
    public OracleContainer oracle = new OracleContainer(DockerImageName.parse("wnameless/oracle-xe-11g"))
            .withInitScript("init_oracle_with_schema.sql")

    Connection createConnection() {
       String url = oracle.getJdbcUrl()
       String user = oracle.getUsername()
       String pass = oracle.getPassword()

       DriverManager.getConnection(url, user, pass)
    }

    DialectName getDialect() {
        DialectName.ORACLE
    }

    String getSchema() {
        return "javers"
    }
}
