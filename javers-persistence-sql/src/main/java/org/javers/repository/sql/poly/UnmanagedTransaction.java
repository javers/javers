package org.javers.repository.sql.poly;

import org.polyjdbc.core.exception.PolyJdbcException;
import org.polyjdbc.core.transaction.Transaction;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author bartosz walacik
 */
public class UnmanagedTransaction extends Transaction {
    private static final Logger logger = getLogger(UnmanagedTransaction.class);

    public UnmanagedTransaction(Connection connection) {
        super(connection);
    }

    @Override
    public void commit() {
        logger.info("connection.commit() skipped, commit should be managed by user");
    }

    @Override
    public void rollback() {
        logger.info("connection.rollback() skipped, commit should be managed by user");
    }

    @Override
    public void close() {
        logger.info("connection.close() skipped, commit should be managed by user");
    }
}
