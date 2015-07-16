package org.javers.repository.sql.integration.oracle

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

class OracleIntegrationTest extends JaversSqlRepositoryE2ETest {

    Connection getConnection() {
       DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "polly", "polly");
    }

    DialectName getDialect() {
        DialectName.ORACLE
    }
}
