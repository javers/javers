package org.javers.repository.sql.integration.opendatabases

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

/**
 * Created by ianagius on 30/09/2016.
 */
class MySqlIntegrationWithSchemaTest extends JaversSqlRepositoryE2ETest {

    Connection createConnection() {
        DriverManager.getConnection("jdbc:mysql://localhost/travis_ci_test", "travis", "");
    }

    DialectName getDialect() {
        DialectName.MYSQL
    }

    String getSchema() {
        return "travis_ci_test"
    }
}