package org.javers.repository.sql.integration.opendatabases

class MySqlIntegrationWithNativeJsonSupportTest extends MySqlIntegrationTest {

    @Override
    boolean isUsingNativeJsonSupport() {
        true
    }
}
