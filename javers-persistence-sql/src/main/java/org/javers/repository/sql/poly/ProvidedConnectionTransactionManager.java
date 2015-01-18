package org.javers.repository.sql.poly;

import org.javers.common.validation.Validate;
import org.javers.repository.sql.ConnectionProvider;
import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.transaction.TransactionManager;

/**
 * @author bartosz walacik
 */
public class ProvidedConnectionTransactionManager implements TransactionManager {

    private final ConnectionProvider connectionProvider;

    public ProvidedConnectionTransactionManager(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Transaction openTransaction() {
        return openTransaction(false);
    }

    @Override
    public Transaction openTransaction(boolean autoCommit) {
        Validate.argumentCheck(autoCommit == false, "autoCommit is not supported");
        return new UnmanagedTransaction(connectionProvider.getConnection());
    }
}
