package org.javers.spring.mongodb;

import com.mongodb.client.MongoDatabase;
import org.javers.core.commit.Commit;
import org.javers.repository.mongo.MongoRepository;
import org.javers.repository.mongo.MongoRepositoryConfiguration;
import org.springframework.data.mongodb.ClientSessionExtractor;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@link MongoRepository} which joins transactions managed by {@link MongoTransactionManager}.
 * <br/><br/>
 *
 * Transaction strategy:
 * <ul>
 *   <li><b>Reads join, never start.</b>
 *       If the application has started a Mongo transaction (e.g. with <code>@Transactional</code>
 *       or <code>TransactionTemplate</code>), reads join it and see snapshots written earlier
 *       in the same, not yet committed transaction.
 *       Without an active transaction, reads run without one.</li>
 *   <li><b>Writes join or start.</b>
 *       <code>javers.commit()</code> joins the application's transaction, or runs in its own, short
 *       Mongo transaction if none is active, so a commit (snapshots and head id) is always atomic.</li>
 * </ul>
 *
 * Only a transaction of the given {@link MongoTransactionManager} is joined,
 * transactions of other transaction managers are ignored.
 * <br/><br/>
 *
 * Transaction synchronization must stay enabled on the {@link MongoTransactionManager} (Spring's default).
 */
class TransactionalMongoRepository extends MongoRepository {
    private final MongoTransactionManager txManager;
    private final TransactionTemplate transactionTemplate;

    TransactionalMongoRepository(MongoDatabase mongo, MongoRepositoryConfiguration config, MongoTransactionManager txManager) {
        super(mongo, config, new SpringMongoSessionProvider(txManager));
        this.txManager = txManager;
        this.transactionTemplate = new TransactionTemplate(txManager);
    }

    TransactionalMongoRepository(MongoRepository source, MongoTransactionManager txManager) {
        super(source, new SpringMongoSessionProvider(txManager));
        this.txManager = txManager;
        this.transactionTemplate = new TransactionTemplate(txManager);
    }

    /**
     * Joins the application's Mongo transaction, or starts a new one if none is active.
     * <br/><br/>
     *
     * When joining, snapshots go to the transaction-local cache (L1) and are promoted
     * to the shared cache (L2) by Spring's <code>afterCommit</code> callback,
     * because only Spring knows when the application's transaction commits.
     * <br/><br/>
     *
     * When Javers starts its own transaction, it puts the commit into L2
     * right after that transaction commits, when <code>executeWithoutResult()</code> returns.
     */
    @Override
    public void persist(Commit commit) {
        if (ClientSessionExtractor.currentTransactionSession(txManager.getDatabaseFactory()).isPresent()) {
            super.persist(commit);
            return;
        }

        transactionTemplate.executeWithoutResult(status -> persistToDb(commit,
                ClientSessionExtractor.currentTransactionSession(txManager.getDatabaseFactory()).get()));
        cacheCommitted(commit);
    }
}
