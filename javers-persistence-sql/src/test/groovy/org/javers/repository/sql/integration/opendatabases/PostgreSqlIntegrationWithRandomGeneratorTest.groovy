package org.javers.repository.sql.integration.opendatabases

class PostgreSqlIntegrationWithRandomGeneratorTest extends PostgreSqlIntegrationTest {

    @Override
    boolean useRandomCommitIdGenerator() {
        true
    }
}
