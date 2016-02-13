package org.javers.repository.sql

import groovy.sql.Sql
import org.javers.core.JaversRepositoryE2ETest
import org.javers.core.cases.Case207Arrays
import org.javers.core.cases.Case208DateTimeTypes
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import org.javers.repository.api.JaversRepository
import org.javers.repository.jql.QueryBuilder

import java.sql.Connection

abstract class JaversSqlRepositoryE2ETest extends JaversRepositoryE2ETest {

    private Connection connection

    protected abstract Connection createConnection()

    protected abstract DialectName getDialect()

    protected Connection getConnection() {
        connection
    }

    @Override
    def setup() {
        clearTables()
        connection.commit()
    }

    @Override
    protected JaversRepository prepareJaversRepository() {
        connection = createConnection()
        connection.setAutoCommit(false)
        return SqlRepositoryBuilder
            .sqlRepository()
            .withConnectionProvider({ connection } as ConnectionProvider)
            .withDialect(getDialect()).build()
    }

    def clearTables() {
        execute("delete  from jv_snapshot")
        execute("delete  from jv_commit")
        execute("delete  from jv_global_id")
        execute("delete  from jv_cdo_class")
    }

    def cleanup() {
        connection.rollback()
        connection.close()
    }

    def execute(String sql) {
        def stmt = connection.createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
    }

    def "should select Head using max CommitId and not table PK"(){
        given:
        def sql = new Sql(connection)
        [
                [3, 3.00],
                [2, 11.02],
                [1, 11.01]
        ].each {
            sql.execute 'insert into jv_commit (commit_pk, commit_id) values (?,?)', it
        }

        when:
        def head = repository.headId
        println head

        then:
        head.majorId == 11
        head.minorId == 2
    }

    def "should not interfere with user transactions"() {
        given:
        def anEntity = new SnapshotEntity(id: 1)

        when:
        javers.commit("author", anEntity)
        connection.rollback()
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

        then:
        !snapshots

        when:
        javers.commit("author", anEntity)
        connection.commit()
        snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

        then:
        snapshots.size() == 1
    }

    //see https://github.com/javers/javers/issues/206
    def "should persist PersistentGlobalId in CdoSnapshot.state"(){
      given:
      def master = new SnapshotEntity(id:1)
      javers.commit("anonymous", master)
      master.valueObjectRef = new DummyAddress("details")

      when:
      javers.commit("anonymous", master)

      then:
      def snapshots = javers.findSnapshots(QueryBuilder.byClass(SnapshotEntity).build())
      snapshots.size() == 2
    }

    /**
     * Case 208
     * see https://github.com/javers/javers/issues/208
     */
    def "should not commit when date/time values are unchanged" () {
        given:
        def obj = new Case208DateTimeTypes("1")

        when:
        javers.commit("anonymous", obj)
        javers.commit("anonymous", obj)

        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId("1", Case208DateTimeTypes).build())

        then:
        snapshots.size() == 1
        snapshots[0].commitId.majorId == 1
    }

    /**
     * Case 207
     * see https://github.com/javers/javers/issues/207
     */
    def "should not commit when Arrays of ValueObjects and ints are unchanged" () {
      given:
        def master = new Case207Arrays.Master(
            id:1,
            array: new Case207Arrays.Detail("details-array"),
            iArray: [1,2]
        )

        javers.commit("anonymous", master)
        javers.commit("anonymous", master)

      when:
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(Case207Arrays.Master).build())

      then:
      snapshots.size() == 1
      snapshots[0].commitId.majorId == 1
    }
}
