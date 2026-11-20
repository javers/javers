package org.javers.spring.mongodb

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.model.SnapshotEntity
import org.javers.repository.mongo.CacheStats
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.AbstractPlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import org.testcontainers.mongodb.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.repository.mongo.MongoRepositoryConfigurationBuilder.mongoRepositoryConfiguration
import static org.javers.spring.mongodb.TransactionalMongoRepositoryBuilder.transactionalMongoRepository

@Testcontainers
class TransactionalMongoCacheIntegrationTest extends Specification {

    @Shared
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.2").withReplicaSet().tap { start() }

    @Shared
    MongoClient mongoClient = MongoClients.create(mongoDBContainer.replicaSetUrl)

    MongoTransactionManager newTxManager() {
        def dbName = "tx_db_" + UUID.randomUUID().toString().replace("-", "")
        new MongoTransactionManager(new SimpleMongoClientDatabaseFactory(mongoClient, dbName))
    }

    Javers transactionalJavers(MongoTransactionManager txManager, int cacheSize = 5000) {
        def javersRepository = transactionalMongoRepository(txManager.databaseFactory.mongoDatabase)
                .withMongoRepositoryConfiguration(mongoRepositoryConfiguration().withCacheSize(cacheSize).build())
                .withTxManager(txManager)
                .build()
        JaversBuilder.javers().registerJaversRepository(javersRepository).build()
    }

    CacheStats currentTransL1Stats(Javers javers) {
        javers.repository.delegate.currentTransactionCacheStats()
    }

    CacheStats l2Stats(Javers javers) {
        javers.repository.delegate.latestSnapshotCacheStats()
    }

    def "should not expose uncommitted transactional write in L2"() {
        given:
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)

        when:
        def statsDuringTx = new TransactionTemplate(txManager).execute({ status ->
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))
            [l1: currentTransL1Stats(javers), l2: l2Stats(javers)]
        })

        then: "write went to L1, not L2"
        statsDuringTx.l1.size() == 1
        statsDuringTx.l2.size() == 1
    }

    def "should populate L2 after transaction commits"() {
        given:
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)

        expect:
        l2Stats(javers).size() == 0

        when:
        new TransactionTemplate(txManager).execute({ status ->
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))
            null
        })

        def latest = javers.getLatestSnapshot(1, SnapshotEntity).get()

        then: "L2 is populated after commit (L1 promoted to L2)"
        l2Stats(javers).size() == 1
        l2Stats(javers).hits() == 1
        latest.getPropertyValue("intProperty") == 1
    }

    def "should not promote L1 to L2 after transaction rollback"() {
        given:
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)

        when:
        def l1DuringTx = new TransactionTemplate(txManager).execute({ status ->
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))
            def l1 = currentTransL1Stats(javers)
            status.setRollbackOnly()
            l1
        })

        then: "L1 had the write before rollback"
        l1DuringTx.size() == 1

        and: "L1 was discarded, not promoted; L2 holds only a 'not found' entry"
        l2Stats(javers).size() == 1
        javers.getLatestSnapshot(1, SnapshotEntity).isEmpty()
    }

    def "should serve the transaction's own write from L1 when reading after a commit in the same transaction"() {
        given:
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)

        when:
        def statsDuringTx = new TransactionTemplate(txManager).execute({ status ->
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))
            def latest = javers.getLatestSnapshot(1, SnapshotEntity).get()

            [l1: currentTransL1Stats(javers), l2: l2Stats(javers), latest: latest]
        })

        then: "second commit read the first write from L1"
        statsDuringTx.l1.hits() == 1

        and: "L2 doesn't have the uncommitted snapshot (it holds only a 'not found' entry)"
        statsDuringTx.l2.size() == 1

        and: "latest snapshot reflects the first commit"
        statsDuringTx.latest.getPropertyValue("intProperty") == 1
    }

    def "should replace an older snapshot in L2 with the one committed in a transaction"() {
        given:
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)
        def entity = new SnapshotEntity(id: 1, intProperty: 1)
        javers.commit("author", entity)

        expect:
        javers.getLatestSnapshot(1, SnapshotEntity).get().getPropertyValue("intProperty") == 1

        when:
        new TransactionTemplate(txManager).execute({ status ->
            entity.intProperty = 2
            javers.commit("author", entity)
            null
        })

        then:
        javers.getLatestSnapshot(1, SnapshotEntity).get().getPropertyValue("intProperty") == 2
    }

    def "should read uncommitted snapshot from the database when cache disabled"() {
        given: "both L1 and L2 are not used"
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager, 0)

        when:
        def result = new TransactionTemplate(txManager).execute({ status ->
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))
            [latest: javers.getLatestSnapshot(1, SnapshotEntity),
             l1    : currentTransL1Stats(javers),
             l2    : l2Stats(javers)]
        })

        then:
        result.latest.isPresent()
        [result.l1, result.l2].every { it.hits() == 0 && it.misses() == 0 && it.size() == 0 }
    }

    def "should not serve stale snapshots when a transaction writes more snapshots than L1 holds"() {
        given: "L1 and L2 hold 5 snapshots, a commit has 7"
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager, 5)
        def ids = 1..6
        def graph = { int value ->
            new SnapshotEntity(id: 0, intProperty: value, listOfEntities: ids.collect { new SnapshotEntity(id: it, intProperty: value) })
        }
        javers.commit("author", graph(1))

        when: "a transaction writes new versions of all of them"
        def valuesReadInTransaction = new TransactionTemplate(txManager).execute({ status ->
            javers.commit("author", graph(2))
            ids.collect { javers.getLatestSnapshot(it, SnapshotEntity).get().getPropertyValue("intProperty") }
        })

        then: "new versions are read in the transaction and after the commit"
        valuesReadInTransaction.every { it == 2 }
        ids.every { javers.getLatestSnapshot(it, SnapshotEntity).get().getPropertyValue("intProperty") == 2 }
    }

    def "should serve a commit from L2 when Javers ran it in its own transaction inside another manager's transaction which rolled back"() {
        given: "L2 holds the first version of the entity"
        def txManager = newTxManager()
        def javers = transactionalJavers(txManager)
        def entity = new SnapshotEntity(id: 1, intProperty: 1)
        javers.commit("author", entity)

        when: "a commit inside another manager's transaction (e.g. JPA) runs in Javers' own Mongo transaction"
        new TransactionTemplate(new OtherTransactionManager()).execute({ status ->
            entity.intProperty = 2
            javers.commit("author", entity)
            status.setRollbackOnly()
            null
        })

        then: "Javers' transaction committed, so L2 serves the new version, although the other transaction rolled back"
        javers.getLatestSnapshot(1, SnapshotEntity).get().getPropertyValue("intProperty") == 2
    }

    /**
     * Stands for a transaction manager of another resource, e.g. JPA.
     */
    static class OtherTransactionManager extends AbstractPlatformTransactionManager {
        @Override
        protected Object doGetTransaction() {
            new Object()
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
        }
    }
}
