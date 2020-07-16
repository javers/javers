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
        names.commitPkSeqName == "jv_commit_pk_seq"

        names.globalIdTableNameWithSchema == "jv_global_id"
        names.globalIdPkSeqName == "jv_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "jv_snapshot"
        names.snapshotTablePkSeqName == "jv_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "jv_commit_property"
    }

    def "should provide default names with schema" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, 's', true, null, null, null, null))

        then:
        names.commitTableNameWithSchema == "s.jv_commit"
        names.commitPkSeqName == "s.jv_commit_pk_seq"

        names.globalIdTableNameWithSchema == "s.jv_global_id"
        names.globalIdPkSeqName == "s.jv_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "s.jv_snapshot"
        names.snapshotTablePkSeqName == "s.jv_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "s.jv_commit_property"
    }

    def "should provide custom table names" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, null, true, "g", "c", "s", "cp"))

        then:
        names.commitTableNameWithSchema == "c"
        names.commitPkSeqName == "jv_commit_pk_seq"

        names.globalIdTableNameWithSchema == "g"
        names.globalIdPkSeqName == "jv_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "s"
        names.snapshotTablePkSeqName == "jv_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "cp"
    }
}
