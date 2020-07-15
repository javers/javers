package org.javers.repository.sql.integration.docker

class MSSqlDockerIntegrationWithCustomTableNamesTest extends MSSqlDockerIntegrationTest {

    @Override
    protected String globalIdTableName() {
        'cust_jv_global_id'
    }

    @Override
    protected String commitTableName() {
        'cust_jv_commit'
    }

    @Override
    protected String snapshotTableName() {
        'cust_jv_snapshot'
    }

    @Override
    protected String commitPropertyTableName() {
        'cust_jv_commit_property'
    }

}
