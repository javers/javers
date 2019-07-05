package org.javers.repository.sql.integration.docker

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest
import org.junit.ClassRule
import org.testcontainers.containers.MSSQLServerContainer
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

class MSSqlDockerIntegrationTest extends JaversSqlRepositoryE2ETest {

    @ClassRule @Shared
    public MSSQLServerContainer mssqlserver = new MSSQLServerContainer();

    Connection createConnection() {
       String url = mssqlserver.jdbcUrl
       String user = mssqlserver.username
       String pass = mssqlserver.password

       DriverManager.getConnection(url, user, pass)
    }

    DialectName getDialect() {
        DialectName.MSSQL
    }

    String getSchema() {
        return null
    }
}
