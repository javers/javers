package org.javers.repository.mongo;

import java.util.Optional;

/**
 * Provides the Mongo transaction started by the application in the current thread, if any.
 * Analogous to ConnectionProvider in javers-persistence-sql.
 *
 * @since 7.12
 */
public interface MongoSessionProvider {

    /**
     * Joins the application's transaction; never starts one.
     */
    Optional<MongoTransaction> currentTransaction();

    static MongoSessionProvider noTransactions() {
        return Optional::empty;
    }
}
