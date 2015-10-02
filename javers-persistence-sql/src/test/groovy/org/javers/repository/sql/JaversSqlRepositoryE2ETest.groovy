package org.javers.repository.sql

import org.javers.core.JaversRepositoryE2ETest
import org.javers.core.model.DateTimeTypes
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder

import java.sql.Connection
import java.sql.DriverManager

import static org.javers.core.JaversBuilder.javers

class JaversSqlRepositoryE2ETest extends JaversRepositoryE2ETest {

    private Connection con

    protected Connection createConnection() {
        DriverManager.getConnection( "jdbc:h2:mem:test" )//TRACE_LEVEL_SYSTEM_OUT=2")
    }

    Connection getConnection() {
        con
    }

    protected DialectName getDialect() {
        DialectName.H2
    }

    @Override
    def setup() {
        con = createConnection()
        con.setAutoCommit(false)

        def connectionProvider = { getConnection() } as ConnectionProvider

        def sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(getDialect()).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
        clearTables()

        getConnection().commit()
    }

    def clearTables() {
        execute("delete  from jv_snapshot")
        execute("delete  from jv_commit")
        execute("delete  from jv_global_id")
        execute("delete  from jv_cdo_class")
    }

    def cleanup() {
        getConnection().rollback()
        getConnection().close()
    }

    def execute(String sql) {
        def stmt = getConnection().createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
    }

    def "should not interfere with user transactions"() {
        given:
        def anEntity = new SnapshotEntity(id: 1)

        when:
        javers.commit("author", anEntity)
        getConnection().rollback()
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

        then:
        !snapshots

        when:
        javers.commit("author", anEntity)
        getConnection().commit()
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
     * see https://github.com/javers/javers/issues/208
     */
    def "should not commit when date/time values are unchanged" () {
        given:
        def obj = new DateTimeTypes("1")

        when:
        javers.commit("anonymous", obj)
        javers.commit("anonymous", obj)

        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId("1", DateTimeTypes).build())

        then:
        snapshots.size() == 1
        snapshots[0].commitId.majorId == 1
    }

}
