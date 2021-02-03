package org.javers.repository.sql.integration.opendatabases

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

class PostgreSqlIntegrationTest extends JaversSqlRepositoryE2ETest {

    @Override
    Connection createConnection() {
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/travis_ci_test", "javers", "javers");
    }

    @Override
    DialectName getDialect() {
        DialectName.POSTGRES
    }

    @Override
    String getSchema() {
        return null
    }

    @Override
    boolean useRandomCommitIdGenerator() {
        false
    }
}
