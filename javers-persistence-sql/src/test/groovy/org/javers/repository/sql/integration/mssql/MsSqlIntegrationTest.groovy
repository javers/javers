package org.javers.repository.sql.integration.mssql

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

class MsSqlIntegrationTest extends JaversSqlRepositoryE2ETest {

    Connection getConnection() {
       DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=polly", "polly", "polly");
    }

    DialectName getDialect() {
        DialectName.MSSQL
    }
}
