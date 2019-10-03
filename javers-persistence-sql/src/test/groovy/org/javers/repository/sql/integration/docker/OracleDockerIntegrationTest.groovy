package org.javers.repository.sql.integration.docker

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest
import org.junit.ClassRule
import org.testcontainers.containers.OracleContainer
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

class OracleDockerIntegrationTest extends JaversSqlRepositoryE2ETest {

    //docker image built by https://github.com/wnameless/docker-oracle-xe-11g.git
    @ClassRule @Shared
    public OracleContainer oracle = new OracleContainer("wnameless/oracle-xe-11g")

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
        return null
    }
}
