package org.javers.repository.sql

import java.sql.Connection
import java.sql.DriverManager

/**
 * @author Ian Agius
 */
class H2SqlRepositoryE2EWithSchemaTest extends H2SqlRepositoryE2ETest {

    Connection createConnection() {
        DriverManager.getConnection("jdbc:h2:mem:WithSchemaTest")
    }

    String getSchema() {
        return "public"
    }
}
