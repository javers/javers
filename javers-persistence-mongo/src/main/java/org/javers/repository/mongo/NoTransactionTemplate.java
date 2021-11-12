package org.javers.repository.mongo;

import java.util.Optional;

public class NoTransactionTemplate implements JaversMongoTransactionTemplate{
    private NoTransactionTemplate() {
    }

    @Override
    public void execute(TransactionCallback callback) {
        callback.doInTransaction(Optional.empty());
    }

    public static NoTransactionTemplate instance() {
        return new NoTransactionTemplate();
    }
}
