package org.javers.repository.sql.integration.opendatabases

/**
 * @author Ian Agius
 */
class MySqlIntegrationWithSchemaTest extends MySqlIntegrationTest {
    String getSchema() {
        return "travis_ci_test"
    }
}