package org.javers.repository.sql.integration.opendatabases

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

/**
 * Created by ianagius on 30/09/2016.
 */
class PostgreSqlIntegrationWithSchemaTest extends JaversSqlRepositoryE2ETest {

    Connection createConnection() {
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/travis_ci_test", "postgres", "");
    }

    DialectName getDialect() {
        DialectName.POSTGRES
    }

    String getSchema() {
        return "public"
    }
}