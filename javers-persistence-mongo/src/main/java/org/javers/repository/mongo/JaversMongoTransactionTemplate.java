package org.javers.repository.mongo;

import com.mongodb.client.ClientSession;

import java.util.Optional;

/**
 * Abstraction based on <code>org.springframework.transaction.support.TransactionTemplate</code>.
 * Allows Javers' {@link MongoRepository} to participate in application's transactions
 * introduced in MongoDB 4.0 and supported in Spring Data MongoDB since 2.1.
 * <br/><br/>
 *
 * For non-transactional (classic) approach &mdash; use {@link NoTransactionTemplate#instance()}
 * <br/><br/>
 *
 * See <a href="https://www.baeldung.com/spring-data-mongodb-transactions">https://www.baeldung.com/spring-data-mongodb-transactions</a>,
 * <a href="https://www.mongodb.com/blog/post/mongodb-multi-document-acid-transactions-general-availability">https://www.mongodb.com/blog/post/mongodb-multi-document-acid-transactions-general-availability</a>
 */
public interface JaversMongoTransactionTemplate {

    @FunctionalInterface
    interface TransactionCallback {
        /**
         * @param clientSession Current session if multi-document ACID transactions
         *                      management is enabled. Empty otherwise.
         */
        void doInTransaction(Optional<ClientSession> clientSession);
    }

    void execute(TransactionCallback callback);
}
