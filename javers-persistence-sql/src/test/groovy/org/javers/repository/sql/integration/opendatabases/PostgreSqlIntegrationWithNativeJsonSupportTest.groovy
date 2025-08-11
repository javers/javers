package org.javers.repository.sql.integration.opendatabases

class PostgreSqlIntegrationWithNativeJsonSupportTest extends PostgreSqlIntegrationTest {

    @Override
    boolean isUsingNativeJsonSupport() {
        true
    }
}
