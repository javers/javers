package org.javers.repository.sql.integration

import java.sql.Connection
import java.sql.DriverManager

class H2IntegrationTest extends BaseSqlIntegrationTest {

    Connection getConnection() {
        DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test")
    }
}
