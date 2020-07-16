package org.javers.repository.sql.schema

import org.javers.repository.sql.SqlRepositoryConfiguration
import spock.lang.Specification

class TableNameProviderTest extends Specification {

    def "should provide default names without schema" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, null, true, null, null, null, null))

        then:
        names.commitTableNameWithSchema == "jv_commit"
        names.commitPkSeqWithSchema == "jv_commit_pk_seq"

        names.globalIdTableNameWithSchema == "jv_global_id"
        names.globalIdPkSeqWithSchema == "jv_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "jv_snapshot"
        names.snapshotTablePkSeqWithSchema == "jv_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "jv_commit_property"
    }

    def "should provide default names with schema" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, 's', true, null, null, null, null))

        then:
        names.commitTableNameWithSchema == "s.jv_commit"
        names.commitPkSeqWithSchema == "s.jv_commit_pk_seq"

        names.globalIdTableNameWithSchema == "s.jv_global_id"
        names.globalIdPkSeqWithSchema == "s.jv_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "s.jv_snapshot"
        names.snapshotTablePkSeqWithSchema == "s.jv_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "s.jv_commit_property"
    }

    def "should provide custom table names" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, null, true, "g", "c", "s", "cp"))

        then:
        names.commitTableNameWithSchema == "c"
        names.commitPkSeqWithSchema == "jv_commit_pk_seq"

        names.globalIdTableNameWithSchema == "g"
        names.globalIdPkSeqWithSchema == "jv_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "s"
        names.snapshotTablePkSeqWithSchema == "jv_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "cp"
    }
}
