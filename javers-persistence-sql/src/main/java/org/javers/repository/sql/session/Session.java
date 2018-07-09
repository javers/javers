package org.javers.repository.sql.session;

import org.javers.common.validation.Validate;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Session implements AutoCloseable{
    private final DialectName dialectName;
    private final Map<String, PreparedStatement> preparedStatements = new HashMap<>();
    private final ConnectionProvider connectionProvider;

    Session(DialectName dialectName, ConnectionProvider connectionProvider) {
        this.dialectName = dialectName;
        this.connectionProvider = connectionProvider;
    }

    public long insert(String queryName, List<Parameter> values, String tableName, String primaryKeyFieldName, String sequenceName) {
        Validate.argumentsAreNotNull(queryName, values, tableName, primaryKeyFieldName, sequenceName);

        InsertQuery insertQuery = null;
        if (dialectName.getPolyDialect().supportsSequences()) {
            insertQuery = new InsertQuery(queryName, values, tableName, primaryKeyFieldName, sequenceName);
        }
        else {
            insertQuery = new InsertQuery(queryName, values, tableName);
        }

        return execute(insertQuery);
    }

    @Override
    public void close() {
        for(PreparedStatement p : preparedStatements.values()) {
            wrapSqlException(() -> p.close());
        }
    }

    private long execute(InsertQuery insertQuery) {
        PreparedStatement statement = getOrCreatePreparedStatement(insertQuery);

        wrapSqlException(() -> insertQuery.injectValuesTo(statement));

        return -1;
    }

    private PreparedStatement getOrCreatePreparedStatement(Query query) {
        if (preparedStatements.containsKey(query.name())) {
            return preparedStatements.get(query.name());
        }

        PreparedStatement statement = wrapSqlException(
            () -> connectionProvider.getConnection().prepareStatement(query.sql()));

        preparedStatements.put(query.name(), statement);

        return statement;
    }

    private <T> T wrapSqlException(SqlAction<T> action) {
        try {
            return action.call();
        } catch (SQLException e) {
            throw new SqlUncheckedException("error while executing SQL", e);
        }
    }

    private void wrapSqlException(SqlVoidAction action) {
        try {
            action.call();
        } catch (SQLException e) {
            throw new SqlUncheckedException("error while executing SQL", e);
        }
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
