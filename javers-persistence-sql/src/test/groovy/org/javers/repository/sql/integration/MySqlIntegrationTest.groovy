package org.javers.repository.sql.integration

import org.javers.core.JaversRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager

class MySqlIntegrationTest extends JaversRepositoryE2ETest {

    Connection getConnection() {
        DriverManager.getConnection("jdbc:mysql://localhost/travis_ci_test", "travis", "");
    }
}
