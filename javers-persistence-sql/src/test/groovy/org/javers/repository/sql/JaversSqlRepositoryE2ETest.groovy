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
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

import static groovyx.gpars.GParsPool.withPool

abstract class JaversSqlRepositoryE2ETest extends JaversRepositoryE2ETest {

    private ThreadLocal<Connection> connection = ThreadLocal.withInitial({createAndInitConnection()})
    private Collection<Connection> connections = new ConcurrentLinkedQueue<>()

    protected abstract Connection createConnection()

    protected abstract DialectName getDialect()

    protected abstract String getSchema()

    protected Connection getConnection() {
        connection.get()
    }

    Connection createAndInitConnection() {
        def connection = createConnection()
        connection.setAutoCommit(false)
        connections.add(connection)
        connection
    }

    @Override
    def setup() {
        clearTables()
    }

    def cleanup() {
        connections.each {
            it.rollback()
            it.close()
        }
    }

    @Override
    protected JaversRepository prepareJaversRepository() {
        SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider({ getConnection() } as ConnectionProvider)
                .withDialect(getDialect())
                .withSchema(getSchema())
                .build()
    }

    def clearTables() {
        execute("delete  from ${schemaPrefix()}jv_snapshot")
        execute("delete  from ${schemaPrefix()}jv_commit")
        execute("delete  from ${schemaPrefix()}jv_commit_property")
        execute("delete  from ${schemaPrefix()}jv_global_id")
        getConnection().commit()
    }

    String schemaPrefix() {
        getSchema() ? getSchema() + "." : ""
    }

    def execute(String sql) {
        def stmt = getConnection().createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
    }

    def "should select Head using max CommitId and not table PK"(){
        given:
        def sql = new Sql(getConnection())
        [
                [3, 3.00],
                [2, 11.02],
                [1, 11.01]
        ].each {
            sql.execute "insert into ${schemaPrefix()}jv_commit (commit_pk, commit_id) values (?,?)", it
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

    def "should persist over 100 snapshots with proper sequence of primary keys"() {
        given:
        (150..1).each{
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: it))
        }

        when:
        def query = QueryBuilder.byInstanceId(1, SnapshotEntity).limit(150).build()
        def snapshots = javers.findSnapshots(query)
        def intPropertyValues = snapshots.collect { it.getPropertyValue("intProperty") }

        then:
        intPropertyValues == 1..150
    }

    def "should allow concurrent updates of different Objects"(){
        given:
        def cnt = new AtomicInteger()
        def threads = 99

        when:
        withPool threads, {
            (1..threads).collectParallel {
                def thread = it
                4.times {
                    javers.commit("author", new SnapshotEntity(id: thread, intProperty: cnt.incrementAndGet()))
                    getConnection().commit()
                }
            }
        }

        then:
        javers.findSnapshots(QueryBuilder.byClass(SnapshotEntity).limit(1000).build()).size() == threads * 4
    }

    def "should allow concurrent updates of the same Object"(){
        given:
        def cnt = new AtomicInteger()
        def sId = 222
        def threads = 99
        //initial commit
        javers.commit("author", new SnapshotEntity(id: sId, intProperty: cnt.incrementAndGet()))
        getConnection().commit()

        when:
        withPool threads, {
            (1..threads).collectParallel {
                4.times {
                    javers.commit("author", new SnapshotEntity(id: sId, intProperty: cnt.incrementAndGet()))
                    getConnection().commit()
                }
            }
        }

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(sId, SnapshotEntity).limit(1000).build()).size() == threads * 4 + 1
    }
}
