package org.javers.repository.sql.integration.opendatabases

import spock.lang.Ignore

/**
 * @author Ian Agius
 */
@Ignore // in MySQL SCHEMA is a synonym for DATABASE
class MySqlIntegrationWithSchemaTest extends MySqlIntegrationTest {
    @Override
    String getSchema() {
        return "travis_ci_test"
    }
}