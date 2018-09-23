package org.javers.repository.sql.session;

import org.javers.repository.sql.ConnectionProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class PreparedStatementExecutor {
    private final PreparedStatement statement;
    private final String rawSql;
    private final String queryName;
    private int executionCount;
    private long executionTotalMillis;

    PreparedStatementExecutor(ConnectionProvider connectionProvider, Query query) {
        this.rawSql = query.rawSQl();
        this.queryName = query.name();
        this.statement = wrapExceptionAndCall(() -> connectionProvider.getConnection().prepareStatement(this.rawSql));
    }

    int getExecutionCount() {
        return executionCount;
    }

    long getExecutionTotalMillis() {
        return executionTotalMillis;
    }

    void execute(Insert insertQuery) {
        runVoidSql(() -> {
            insertQuery.injectValuesTo(statement);
            statement.executeUpdate();
        });
    }

    long executeQueryForLong(Select select) {
        return runSql(() -> {
            select.injectValuesTo(statement);
            ResultSet rset = statement.executeQuery();
            rset.next();
            return rset.getLong(1);
        });
    }

    void close() {
       wrapExceptionAndCall(() -> statement.close());
    }

    private <T> T runSql(SqlAction<T> action) {
        long start = System.currentTimeMillis();

        T result =  wrapExceptionAndCall(action);

        executionCount++;
        executionTotalMillis += System.currentTimeMillis() - start;
        return result;
    }

    private void runVoidSql(SqlVoidAction action) {
        long start = System.currentTimeMillis();

        wrapExceptionAndCall(action);

        executionCount++;
        executionTotalMillis += System.currentTimeMillis() - start;
    }

    private void wrapExceptionAndCall(SqlVoidAction action) {
        try {
            action.call();
        } catch (SQLException e) {
            throw new SqlUncheckedException("error while executing SQL", e);
        }
    }

    private <T> T wrapExceptionAndCall(SqlAction<T> action) {
        try {
            return action.call();
        } catch (SQLException e) {
            throw new SqlUncheckedException("error while executing SQL", e);
        }
    }

    String printStats() {
        return "statement '" + queryName + "' executed " + executionCount +
               " time(s), in " + executionTotalMillis + " millis, SQL: " + rawSql;
    }

    @FunctionalInterface
    private interface SqlAction<T> {
        T call() throws SQLException;
    }

    @FunctionalInterface
    private interface SqlVoidAction {
        void call() throws SQLException;
    }
}
