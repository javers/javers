package org.javers.repository.sql.session;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bartosz.walacik
 */
public class Session implements AutoCloseable {
    public static final String SQL_LOGGER_NAME = "org.javers.SQL";
    private static final Logger logger = LoggerFactory.getLogger(SQL_LOGGER_NAME);

    private final Dialect dialect;
    private final Map<String, PreparedStatementExecutor> statementExecutors = new HashMap<>();
    private final ConnectionProvider connectionProvider;

    Session(DialectName dialectName, ConnectionProvider connectionProvider) {
        this.dialect = Dialects.fromName(dialectName);
        this.connectionProvider = connectionProvider;
    }

    public long insert(String queryName, List<Parameter> parameters, String tableName, String primaryKeyFieldName, String sequenceName) {
        Validate.argumentsAreNotNull(queryName, parameters, tableName, primaryKeyFieldName, sequenceName);

        if (dialect.supportsSequences()) {
            KeyGenerator.Sequence seq = dialect.getKeyGenerator();
            long newId = queryForLong(new Select("next from seq "+ sequenceName, seq.nextFromSequenceAsSelect(sequenceName)));

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

            KeyGenerator.Autoincrement autoincrement = dialect.getKeyGenerator();
            long lastId = queryForLong(new Select("last autoincrement id", autoincrement.lastInsertedAutoincrement()));

            return lastId;
        }
    }

    private long queryForLong(Select select) {
        PreparedStatementExecutor executor = getOrCreatePreparedStatement(select);
        return executor.executeQueryForLong(select);
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

    private void logStats() {
        logger.debug("SQL session finished. Executed {} statement(s) in {} millis.",
                statementExecutors.values().stream().mapToInt(i -> i.getExecutionCount()).sum(),
                statementExecutors.values().stream().mapToLong(i -> i.getExecutionTotalMillis()).sum());

        statementExecutors.values().stream().forEach(e -> logger.debug("* "+e.printStats()));
    }
}
