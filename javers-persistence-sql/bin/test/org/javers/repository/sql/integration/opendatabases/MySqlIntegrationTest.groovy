package org.javers.repository.sql.integration.opendatabases

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

class MySqlIntegrationTest extends JaversSqlRepositoryE2ETest {

    @Override
    Connection createConnection() {
        DriverManager.getConnection("jdbc:mysql://localhost/travis_ci_test", "javers", "javers");
    }

    @Override
    DialectName getDialect() {
        DialectName.MYSQL
    }

    @Override
    String getSchema() {
        return null
    }

}
