package org.javers.spring.mongodb

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.javers.core.Javers
import org.javers.core.diff.changetype.PropertyChange
import org.javers.core.model.SnapshotEntity
import org.javers.repository.mongo.MongoRepository
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.mongodb.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

/**
 * Covers the bug fixed by threading {@code ClientSession} through Mongo read queries
 * in {@link TransactionalMongoRepository}: before the fix, a commit made earlier in a
 * still-open transaction was invisible to a later read made in the very same transaction
 * (because reads always queried outside of it), which broke the diff algorithm for
 * multiple commits issued within one multi-document transaction.
 * <br/><br/>
 * Requires Docker (a real, replica-set-enabled MongoDB is needed for transactions).
 */
@Testcontainers
class TransactionalMongoRepositoryIntegrationTest extends Specification {

    @Shared
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.2").withReplicaSet().tap { start() }

    @Shared
    MongoClient mongoClient = MongoClients.create(mongoDBContainer.replicaSetUrl)

    private static class TxFixture {
        Javers javers
        MongoRepository mongoRepository
        TransactionTemplate txTemplate
    }

    TxFixture newTransactionalFixture() {
        def dbName = "tx_db_" + UUID.randomUUID().toString().replace("-", "")
        def mongoRepository = new MongoRepository(mongoClient.getDatabase(dbName))
        def databaseFactory = new SimpleMongoClientDatabaseFactory(mongoClient, dbName)
        def txManager = new MongoTransactionManager(databaseFactory)

        def javers = TransactionalMongoJaversBuilder.javers()
                .registerJaversRepository(mongoRepository)
                .withTxManager(txManager)
                .build()

        new TxFixture(javers: javers, mongoRepository: mongoRepository, txTemplate: new TransactionTemplate(txManager))
    }

    def "a second commit made within the same still-open transaction should see the first commit's state"() {
        given:
        def fixture = newTransactionalFixture()

        when: "both commits happen inside one single outer transaction"
        def commits = fixture.txTemplate.execute({ status ->
            def entity = new SnapshotEntity(id: 1, intProperty: 1)
            def commit1 = fixture.javers.commit("author", entity)

            entity.intProperty = 2
            def commit2 = fixture.javers.commit("author", entity)

            [commit1, commit2]
        })

        then: "the second commit must detect the change made by the first commit of the same transaction"
        commits[0].snapshots.size() == 1
        commits[1].snapshots.size() == 1
        commits[1].snapshots[0].getPropertyValue("intProperty") == 2
        commits[1].diff.changes.size() == 1
        commits[1].diff.changes[0] instanceof PropertyChange
        (commits[1].diff.changes[0] as PropertyChange<Integer>).left == 1 // important
        (commits[1].diff.changes[0] as PropertyChange<Integer>).right == 2
    }

    def "getHeadId() should ignore an open, not-yet-committed transaction"() {
        given:
        def fixture = newTransactionalFixture()

        when: "a commit happens inside an open transaction that never commits to the database"
        fixture.txTemplate.execute({ status ->
            fixture.javers.commit("author", new SnapshotEntity(id: 1))
            fixture.mongoRepository.getHeadId() == null
            status.setRollbackOnly()
            null
        })

        then: "the head id must not reflect the rolled-back commit"
        fixture.mongoRepository.getHeadId() == null

        when: "a commit happens inside a transaction that does commit"
        fixture.txTemplate.execute({ status ->
            fixture.javers.commit("author", new SnapshotEntity(id: 2))
            fixture.mongoRepository.getHeadId() == null
            null
        })

        then:
        fixture.mongoRepository.getHeadId() != null
    }
}
