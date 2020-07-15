package org.javers.repository.sql

class H2SqlRepositoryE2EWithCustomTableNames extends H2SqlRepositoryE2EWithSchemaTest {

    def setupSpec() {
        globalIdTableName = "custom_globalIdTable"
        commitTableName = "custom_commitTable"
        commitPropertyTableName = "custom_commitPropertyTable"
        snapshotTableName = "custom_snapshotTable"
    }
}
