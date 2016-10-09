package org.javers.repository.sql.integration.mssql

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

/**
 * Created by ianagius on 30/09/2016.
 */
class MsSqlIntegrationWithSchemaTest extends JaversSqlRepositoryE2ETest {

    Connection createConnection() {
        DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=polly", "polly", "polly");
    }

    DialectName getDialect() {
        DialectName.MSSQL
    }

    String getSchema() {
        return "travis_ci_test"
    }
}