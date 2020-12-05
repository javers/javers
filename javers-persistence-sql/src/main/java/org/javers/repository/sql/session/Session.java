package org.javers.repository.sql.session;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.session.KeyGenerator.SequenceAllocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author bartosz.walacik
 */
public class Session implements AutoCloseable {
    public static final String SQL_LOGGER_NAME = "org.javers.SQL";
    private static final Logger logger = LoggerFactory.getLogger(SQL_LOGGER_NAME);

    private final Dialect dialect;
    private final Map<String, PreparedStatementExecutor> statementExecutors = new HashMap<>();
    private final ConnectionProvider connectionProvider;
    private final String sessionName;
    private final KeyGenerator keyGenerator;

    Session(Dialect dialect, KeyGenerator keyGenerator, ConnectionProvider connectionProvider, String sessionName) {
        this.dialect = dialect;
        this.connectionProvider = connectionProvider;
        this.sessionName = sessionName;
        this.keyGenerator = keyGenerator;
    }

    public SelectBuilder select(String selectClauseSQL) {
        return new SelectBuilder(this, selectClauseSQL);
    }

    public InsertBuilder insert(String queryName) {
        return new InsertBuilder(this, queryName);
    }

    long executeInsertAndGetSequence(String queryName, List<Parameter> parameters, String tableName, String primaryKeyFieldName, String sequenceName) {
        Validate.argumentsAreNotNull(queryName, parameters, tableName, primaryKeyFieldName, sequenceName);

        if (dialect.supportsSequences()) {
            long newId = keyGenerator.generateKey(sequenceName, this);

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

            return keyGenerator.getKeyFromLastInsert(this);
        }
    }

    void executeInsert(String queryName, List<Parameter> parameters, String tableName, String primaryKeyFieldName, String sequenceName) {
        Validate.argumentsAreNotNull(queryName, parameters, tableName);

        if (dialect.supportsSequences() && sequenceName != null) {
            String nextFromSequenceExpression = ((SequenceAllocation) keyGenerator).nextFromSequenceAsSQLExpression(sequenceName);

            Insert insertQuery = new Insert(
                    queryName,
                    Lists.add(parameters, new Parameter.InlinedParameter(primaryKeyFieldName, nextFromSequenceExpression + " * 100")),
                    tableName);

            execute(insertQuery);
        }
        else {
            Insert insertQuery = new Insert(queryName, parameters, tableName);
            execute(insertQuery);
        }
    }

    long executeQueryForLong(Select select) {
        PreparedStatementExecutor executor = getOrCreatePreparedStatement(select);
        return executor.executeQueryForLong(select);
    }

    Optional<BigDecimal> executeQueryForOptionalBigDecimal(Select select) {
        PreparedStatementExecutor executor = getOrCreatePreparedStatement(select);
        return executor.executeQueryForOptionalBigDecimal(select);
    }

    Optional<Long> executeQueryForOptionalLong(Select select) {
        PreparedStatementExecutor executor = getOrCreatePreparedStatement(select);
        return executor.executeQueryForOptionalLong(select);
    }

    <T> List<T> executeQuery(Select select, ObjectMapper<T> objectMapper) {
        PreparedStatementExecutor executor = getOrCreatePreparedStatement(select);
        return executor.executeQuery(select, objectMapper);
    }

    private void execute(Insert insertQuery) {
        PreparedStatementExecutor executor = getOrCreatePreparedStatement(insertQuery);
        executor.execute(insertQuery);
    }

    @Override
    public void close() {
        statementExecutors.values().stream().forEach(p -> p.close());
        logStats();
    }

    private PreparedStatementExecutor getOrCreatePreparedStatement(Query query) {
        if (statementExecutors.containsKey(query.name())) {
            return statementExecutors.get(query.name());
        }

        PreparedStatementExecutor executor = new PreparedStatementExecutor(connectionProvider, query);

        statementExecutors.put(query.name(), executor);

        return executor;
    }

    public void logStats() {
        logger.trace("SQL session '" + sessionName + "' finished. {} statement(s) executed in {} millis.",
                statementExecutors.values().stream().mapToInt(i -> i.getExecutionCount()).sum(),
                statementExecutors.values().stream().mapToLong(i -> i.getExecutionTotalMillis()).sum());

        List<PreparedStatementExecutor> executors = new ArrayList<>(statementExecutors.values());
        Collections.sort(executors, (e1, e2) -> e2.getExecutionTotalMillis() > e1.getExecutionTotalMillis() ? 1 : -1);

        if (logger.isTraceEnabled()) {
            executors.forEach(e -> logger.trace("* " + e.printStats()));
        }
    }

    Dialect getDialect() {
        return dialect;
    }

}
