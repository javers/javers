package org.javers.repository.sql

class H2SqlRepositoryE2EWithCustomTableNames extends H2SqlRepositoryE2EWithSchemaTest {

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
