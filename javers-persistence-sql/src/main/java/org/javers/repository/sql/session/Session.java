package org.javers.repository.sql.session;

import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bartosz.walacik
 */
public class Session implements AutoCloseable{
    private final Dialect dialect;
    private final Map<String, PreparedStatement> preparedStatements = new HashMap<>();
    private final ConnectionProvider connectionProvider;

    Session(DialectName dialectName, ConnectionProvider connectionProvider) {
        this.dialect = Dialect.fromName(dialectName);
        this.connectionProvider = connectionProvider;
    }

    public long insert(String queryName, List<Parameter> parameters, String tableName, String primaryKeyFieldName, String sequenceName) {
        Validate.argumentsAreNotNull(queryName, parameters, tableName, primaryKeyFieldName, sequenceName);

        if (dialect.supportsSequences()) {
            long newId = queryForLong(new Select(sequenceName, dialect.nextFromSequence(sequenceName)));

            Insert insertQuery = new Insert(
                    queryName,
                    Lists.add(parameters, new Parameter.LongParameter(primaryKeyFieldName, newId)),
                    tableName);

            execute(insertQuery);

            return newId;
        }
        else {
            Insert insertQuery = new Insert(queryName, parameters, tableName);

            execute(insertQuery);

            long lastId = queryForLong(new Select(sequenceName, dialect.lastInsertedAutoincrement()));

            return lastId;
        }
    }

    private long queryForLong(Select select) {
        PreparedStatement statement = getOrCreatePreparedStatement(select);

        wrapSqlException(() -> select.injectValuesTo(statement));

        return wrapSqlException(() -> {
            ResultSet rset = statement.executeQuery();
            rset.next();
            return rset.getLong(1);
        });
    }

    @Override
    public void close() {
        for(PreparedStatement p : preparedStatements.values()) {
            wrapSqlException(() -> p.close());
        }
    }

    private void execute(Insert insertQuery) {
        PreparedStatement statement = getOrCreatePreparedStatement(insertQuery);

        wrapSqlException(() -> insertQuery.injectValuesTo(statement));

        wrapSqlException(() -> statement.executeUpdate());
    }

    private PreparedStatement getOrCreatePreparedStatement(Query query) {
        if (preparedStatements.containsKey(query.name())) {
            return preparedStatements.get(query.name());
        }

        PreparedStatement statement = wrapSqlException(
            () -> connectionProvider.getConnection().prepareStatement(query.rawSQl()));

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

    class RegularSequenceDef extends Parameter {
        private final String sequenceName;

        RegularSequenceDef(String primaryKeyFieldName, String sequenceName) {
            super(primaryKeyFieldName, null);
            this.sequenceName = sequenceName;
        }

        @Override
        public String getRawSqlRepresentation() {
            return dialect.nextFromSequence(sequenceName);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
        }
    }
}
