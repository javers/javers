package org.javers.spring.mongodb

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.bson.Document
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.PropertyChange
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.mongodb.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.spring.mongodb.TransactionalMongoRepositoryBuilder.transactionalMongoRepository

/**
 * Covers the bug fixed by threading {@code ClientSession} through Mongo read queries
 * in {@link TransactionalMongoRepository}: before the fix, a commit made earlier in a
 * still-open transaction was invisible to a later read made in the very same transaction
 * (because reads always queried outside of it), which broke the diff algorithm for
 * multiple commits issued within one multi-document transaction.
 */
@Testcontainers
class TransactionalMongoRepositoryIntegrationTest extends Specification {

    @Shared
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.2").withReplicaSet().tap { start() }

    @Shared
    MongoClient mongoClient = MongoClients.create(mongoDBContainer.replicaSetUrl)

    long mongoTotalStarted() {
        mongoClient.getDatabase("admin")
                .runCommand(new Document("serverStatus", 1))
                .get("transactions", Document)
                .getLong("totalStarted")
    }

    MongoTransactionManager newTxManager() {
        def dbName = "tx_db_" + UUID.randomUUID().toString().replace("-", "")
        new MongoTransactionManager(new SimpleMongoClientDatabaseFactory(mongoClient, dbName))
    }

    Javers transactionalJavers(MongoTransactionManager txManager) {
        def javersRepository = transactionalMongoRepository(txManager.databaseFactory.mongoDatabase)
                .withTxManager(txManager)
                .build()
        JaversBuilder.javers().registerJaversRepository(javersRepository).build()
    }

    def "should see the first commit's state in a second commit made within the same still-open transaction"() {
        given:
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)

        when: "both commits happen inside one single outer transaction"
        def result = new TransactionTemplate(txManager).execute({ status ->
            def entity = new SnapshotEntity(id: 1, intProperty: 1)
            def commit1 = javers.commit("author", entity)

            def latestAfterCommit1 = javers.getLatestSnapshot(entity.id, SnapshotEntity)

            entity.intProperty = 2
            def commit2 = javers.commit("author", entity)

            [commit1: commit1, commit2: commit2, latestAfterCommit1: latestAfterCommit1]
        })

        then: "a read between commits sees the first commit's state within the same transaction"
        result.latestAfterCommit1.present
        result.latestAfterCommit1.get().getPropertyValue("intProperty") == 1

        and: "the second commit detects the change made by the first commit"
        result.commit1.snapshots.size() == 1
        result.commit2.snapshots.size() == 1
        result.commit2.snapshots[0].getPropertyValue("intProperty") == 2
        result.commit2.diff.changes.size() == 1
        result.commit2.diff.changes[0] instanceof PropertyChange
        (result.commit2.diff.changes[0] as PropertyChange<Integer>).left == 1
        (result.commit2.diff.changes[0] as PropertyChange<Integer>).right == 2
    }

    def "should ignore an open, not-yet-committed transaction in getHeadId()"() {
        given:
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)

        when: "a commit happens inside an open transaction that never commits to the database"
        new TransactionTemplate(txManager).execute({ status ->
            javers.commit("author", new SnapshotEntity(id: 1))
            status.setRollbackOnly()
            null
        })

        then: "the head id must not reflect the rolled-back commit"
        javers.repository.delegate.getHeadId() == null

        when: "a commit happens inside a transaction that does commit"
        new TransactionTemplate(txManager).execute({ status ->
            javers.commit("author", new SnapshotEntity(id: 2))
            null
        })

        then:
        javers.repository.delegate.getHeadId() != null
    }

    def "should not open a new Mongo transaction when reading outside an active transaction"() {
        given:
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)
        def entity = new SnapshotEntity(id: 1, intProperty: 1)

        and: "some data committed in a transaction"
        new TransactionTemplate(txManager).execute({ status ->
            javers.commit("author", entity)
            null
        })

        when: "reading outside any transaction"
        def txCountBefore = mongoTotalStarted()
        javers.findSnapshots(QueryBuilder.byInstance(entity).build())
        javers.getLatestSnapshot(entity.id, SnapshotEntity)
        def txCountAfter = mongoTotalStarted()

        then: "no new Mongo transaction was opened"
        txCountAfter == txCountBefore
    }

    def "should run a commit made outside a transaction in its own Mongo transaction"() {
        given:
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)
        def txCountBefore = mongoTotalStarted()

        when:
        javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))

        then: "exactly one Mongo transaction was started for the commit"
        mongoTotalStarted() == txCountBefore + 1

        and: "the commit is persisted and served from the cache after its transaction committed"
        javers.getLatestSnapshot(1, SnapshotEntity).get().getPropertyValue("intProperty") == 1
    }
}
