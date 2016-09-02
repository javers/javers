package org.javers.repository.sql.integration.oracle

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

/**
 * Created by ianagius on 30/09/2016.
 */
class OracleIntegrationWithSchemaTest extends JaversSqlRepositoryE2ETest {

    Connection createConnection() {
        DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "polly", "polly");
    }

    DialectName getDialect() {
        DialectName.ORACLE
    }

    String getSchema() {
        return "public"
    }
}
