package org.javers.spring.mongodb

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.javers.core.Javers
import org.javers.core.JaversBuilder
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
 * Javers joins the application's Mongo transaction.
 */
@Testcontainers
class TransactionalMongoSessionProviderTest extends Specification {

    @Shared
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.2").withReplicaSet().tap { start() }

    @Shared
    MongoClient mongoClient = MongoClients.create(mongoDBContainer.replicaSetUrl)

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

    def query(int id) {
        QueryBuilder.byInstanceId(id, SnapshotEntity).build()
    }

    def "should read uncommitted snapshots in the transaction, and not persist them on rollback"() {
        given:
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)

        when:
        def snapshotsReadInTransaction = new TransactionTemplate(txManager).execute({ status ->
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))
            def found = javers.findSnapshots(query(1))
            status.setRollbackOnly()
            found
        })

        then: "the snapshot was visible in the transaction"
        snapshotsReadInTransaction.size() == 1

        and: "it was not persisted, the transaction was rolled back"
        javers.findSnapshots(query(1)).isEmpty()
    }
}
