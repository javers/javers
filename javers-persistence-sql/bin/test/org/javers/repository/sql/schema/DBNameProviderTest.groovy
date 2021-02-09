package org.javers.repository.sql.schema

import org.javers.repository.sql.SqlRepositoryConfiguration
import spock.lang.Specification

class DBNameProviderTest extends Specification {

    def "should provide default names without schema" () {
        when:
        def names = new DBNameProvider(
                new SqlRepositoryConfiguration(false, 
						null,
						true,
						null,
						null,
						null, 
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null))

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
        def names = new DBNameProvider(
		new SqlRepositoryConfiguration(false, 
						's',
						true,
						null,
						null,
						null, 
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null))

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
        def names = new DBNameProvider(
		new SqlRepositoryConfiguration(false, 
						null,
						true,
						"g",
						"c",
						"s", 
						"cp",
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null))

        then:
        names.commitTableNameWithSchema == "c"
        names.commitPkSeqName.toString() == "c_pk_seq"

        names.globalIdTableNameWithSchema == "g"
        names.globalIdPkSeqName.toString() == "g_pk_seq"

        names.snapshotTableNameWithSchema == "s"
        names.snapshotTablePkSeqName.toString() == "s_pk_seq"

        names.commitPropertyTableNameWithSchema == "cp"
    }
    
    def "should provide custom column names" () {
    when:
    def names = new DBNameProvider(
	new SqlRepositoryConfiguration(false, 
					null,
					true,
					"g",
					"c",
					"s", 
					"cp",
					"a",
					"b",
					"c",
					"d",
					"e",
					"f",
					"g",
					"h",
					"i",
					"j",
					"l",
					"m",
					"n",
					"o",
					"p",
					"q",
					"r",
					"s",
					"t",
					"u",
					"v"))

	then:
	names.commitTableNameWithSchema == "c"
	names.commitPkSeqName.toString() == "c_pk_seq"

	names.globalIdTableNameWithSchema == "g"
	names.globalIdPkSeqName.toString() == "g_pk_seq"

	names.snapshotTableNameWithSchema == "s"
	names.snapshotTablePkSeqName.toString() == "s_pk_seq"

	names.commitPropertyTableNameWithSchema == "cp"

	names.globalIdPKColunmName == "a"
	names.globalIdLocalIdColumnName == "b"
	names.globalIdFragmentColumnName == "c"
	names.globalIdTypeNameColumnName == "d"
	names.globalIdOwnerIDFKColumnName == "e"
	names.commitPKColumnName == "f"
	names.commitAuthorColumnName == "g"
	names.commitCommitDateColumnName == "h"
	names.commitCommitDateInstantColumnName == "i"
	names.commitCommitIdColumName == "j"
	names.commitPropertyCommitFKColumnName == "l"
	names.commitPropertyNameColumnName == "m"
	names.commitPropertyValueColumnName == "n"
	names.snapshotPKColumnName == "o"
	names.snapshotCommitFKColumnName == "p"
	names.snapshotGlobalIdFKColumnName == "q"
	names.snapshotTypeColumnName == "r"
	names.snapshotVersionColumnName == "s"
	names.snapshotStateColumnName == "t"
	names.snapshotChangedColumnName == "u"
	names.snapshotManagedTypeColumnName  == "v"
   }
   
   def "should use default column names" () {
        when:
        def names = new DBNameProvider(
		new SqlRepositoryConfiguration(false, 
						null,
						true,
						null,
						null,
						null, 
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null,
						null))

        then:

    	names.globalIdPKColunmName == "global_id_pk"
	names.globalIdLocalIdColumnName == "local_id"
	names.globalIdFragmentColumnName == "fragment"
	names.globalIdTypeNameColumnName == "type_name"
	names.globalIdOwnerIDFKColumnName == "owner_id_fk"
	names.commitPKColumnName == "commit_pk"
	names.commitAuthorColumnName == "author"
	names.commitCommitDateColumnName == "commit_date"
	names.commitCommitDateInstantColumnName == "commit_date_instant"
	names.commitCommitIdColumName == "commit_id"
	names.commitPropertyCommitFKColumnName == "commit_fk"
	names.commitPropertyNameColumnName == "property_name"
	names.commitPropertyValueColumnName == "property_value"
	names.snapshotPKColumnName == "snapshot_pk"
	names.snapshotCommitFKColumnName == "commit_fk"
	names.snapshotGlobalIdFKColumnName == "global_id_fk"
	names.snapshotTypeColumnName == "type"
	names.snapshotVersionColumnName == "version"
	names.snapshotStateColumnName == "state"
	names.snapshotChangedColumnName == "changed_properties"
	names.snapshotManagedTypeColumnName  == "managed_type"

    }
}
