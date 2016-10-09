package org.javers.repository.sql.integration.opendatabases

/**
 * @author Ian Agius
 */
class PostgreSqlIntegrationWithSchemaTest extends PostgreSqlIntegrationTest {

    String getSchema() {
        return "j_some"
    }
}