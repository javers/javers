package org.springframework.data.mongodb;

import com.mongodb.client.ClientSession;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;

/**
 * this class exists because {@link MongoResourceHolder} is not public
 */
public class ClientSessionExtractor {

    /**
     * Session of the Mongo transaction bound to the current thread for the given factory, if one is active.
     */
    public static Optional<ClientSession> currentTransactionSession(MongoDatabaseFactory dbFactory) {
        Object resource = TransactionSynchronizationManager.getResource(dbFactory);
        return resource instanceof MongoResourceHolder holder && holder.hasActiveTransaction()
                ? Optional.of(holder.getSession()) : Optional.empty();
    }
}
