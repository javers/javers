package org.javers.repository.sql.integration.docker

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest
import org.junit.ClassRule
import org.testcontainers.containers.MySQLContainer
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

class MySqlDockerIntegrationWithCustomTableNamesTest extends MySqlDockerIntegrationTest {

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
