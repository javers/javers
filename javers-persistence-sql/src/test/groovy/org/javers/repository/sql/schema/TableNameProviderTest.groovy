package org.javers.repository.sql.schema

import org.javers.repository.sql.SqlRepositoryConfiguration
import spock.lang.Specification

class TableNameProviderTest extends Specification {

    def "should provide default names without schema" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, null, true, false, null, null, null, null))

        then:
        names.commitTableNameWithSchema == "jv_commit"
        names.commitPkSeqName.toString() == "jv_commit_pk_seq"

        names.globalIdTableNameWithSchema == "jv_global_id"
        names.globalIdPkSeqName.toString() == "jv_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "jv_snapshot"
        names.snapshotTablePkSeqName.toString() == "jv_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "jv_commit_property"
    }

    def "should provide default names with schema" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, 's', true, false, null, null, null, null))

        then:
        names.commitTableNameWithSchema == "s.jv_commit"
        names.commitPkSeqName.toString() == "s.jv_commit_pk_seq"

        names.globalIdTableNameWithSchema == "s.jv_global_id"
        names.globalIdPkSeqName.toString() == "s.jv_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "s.jv_snapshot"
        names.snapshotTablePkSeqName.toString() == "s.jv_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "s.jv_commit_property"
    }

    def "should provide custom table names" () {
        when:
        def names = new TableNameProvider(
                new SqlRepositoryConfiguration(false, null, true, false, "g", "c", "s", "cp"))

        then:
        names.commitTableNameWithSchema == "c"
        names.commitPkSeqName.toString() == "jv_commit_pk_seq"

        names.globalIdTableNameWithSchema == "g"
        names.globalIdPkSeqName.toString() == "jv_global_id_pk_seq"

        names.snapshotTableNameWithSchema == "s"
        names.snapshotTablePkSeqName.toString() == "jv_snapshot_pk_seq"

        names.commitPropertyTableNameWithSchema == "cp"
    }
}
