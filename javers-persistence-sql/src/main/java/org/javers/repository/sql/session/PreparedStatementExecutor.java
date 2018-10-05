package org.javers.repository.sql.session;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.string.ToStringBuilder;
import org.javers.repository.sql.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

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
        return executeQueryForValue(select, resultSet -> resultSet.getLong(1));
    }

    BigDecimal executeQueryForBigDecimal(Select select) {
        return executeQueryForValue(select, resultSet -> resultSet.getBigDecimal(1));
    }

    private <T> T executeQueryForValue(Select select, ObjectMapper<T> objectMapper) {
        return runSql(() -> {
            select.injectValuesTo(statement);
            ResultSet rset = statement.executeQuery();
            rset.next();
            return objectMapper.get(rset);
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
            throw new JaversException(JaversExceptionCode.SQL_EXCEPTION, e.getMessage(), rawSql);
        }
    }

    private <T> T wrapExceptionAndCall(SqlAction<T> action) {
        try {
            return action.callAndGet();
        } catch (SQLException e) {
            throw new JaversException(JaversExceptionCode.SQL_EXCEPTION, e.getMessage(), rawSql);
        }
    }

    String printStats() {
        return ToStringBuilder.rPad(queryName, 32) + " executed " + executionCount +
               " time(s) in " + executionTotalMillis + " millis, SQL: " + rawSql;
    }

    @FunctionalInterface
    private interface SqlAction<T> {
        T callAndGet() throws SQLException;
    }

    @FunctionalInterface
    private interface SqlVoidAction {
        void call() throws SQLException;
    }

    @FunctionalInterface
    private interface ObjectMapper<T> {
        T get(ResultSet resultSet) throws SQLException;
    }
}
