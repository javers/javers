package org.javers.repository.sql.integration

import org.javers.repository.sql.DialectName

import java.sql.Connection
import java.sql.DriverManager

class PostgreSqlIntegrationTest extends BaseSqlIntegrationTest {

    Connection getConnection() {
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/travis_ci_test", "postgres", "");
    }

    DialectName getDialect() {
        DialectName.POSTGRES
    }
}
