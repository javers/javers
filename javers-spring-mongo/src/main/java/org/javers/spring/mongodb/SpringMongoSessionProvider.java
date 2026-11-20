package org.javers.spring.mongodb;

import com.mongodb.client.ClientSession;
import org.javers.repository.mongo.MongoSessionProvider;
import org.javers.repository.mongo.MongoTransaction;
import org.springframework.data.mongodb.ClientSessionExtractor;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Looks up the active Mongo transaction of the given {@link MongoTransactionManager}. Never starts one,
 * starting a transaction for writes is done by {@link TransactionalMongoRepository#persist}.
 */
class SpringMongoSessionProvider implements MongoSessionProvider {
    private final MongoTransactionManager txManager;
    private final Map<ClientSession, MongoTransaction> transactions = new ConcurrentHashMap<>();

    SpringMongoSessionProvider(MongoTransactionManager txManager) {
        this.txManager = txManager;
    }

    @Override
    public Optional<MongoTransaction> currentTransaction() {
        return ClientSessionExtractor.currentTransactionSession(txManager.getDatabaseFactory())
                .map(session -> transactions.computeIfAbsent(session, this::begin));
    }

    private MongoTransaction begin(ClientSession session) {
        MongoTransaction tx = new MongoTransaction(session);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                tx.afterCommit();
            }

            @Override
            public void afterCompletion(int status) {
                transactions.remove(session);
            }
        });
        return tx;
    }
}
