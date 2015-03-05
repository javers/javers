package org.javers.repository.sql.integration

import org.javers.core.JaversRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

class PostgreSqlIntegrationTest extends JaversRepositoryE2ETest {

    Connection getConnection() {
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/travis_ci_test", "postgres", "");
    }
}
