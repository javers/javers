package org.javers.repository.sql.session;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.polyjdbc.core.query.InsertQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    Session(DialectName dialectName, ConnectionProvider connectionProvider) {
        this.dialect = Dialects.fromName(dialectName);
        this.connectionProvider = connectionProvider;
    }

    public InsertBuilder insert(String queryName) {
        return new InsertBuilder(queryName);
    }

    long insertAndGetSequence(String queryName, List<Parameter> parameters, String tableName, String primaryKeyFieldName, String sequenceName) {
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

    void insert(String queryName, List<Parameter> parameters, String tableName, String primaryKeyFieldName, String sequenceName) {
        Validate.argumentsAreNotNull(queryName, parameters, tableName, primaryKeyFieldName, sequenceName);

        if (dialect.supportsSequences()) {
            KeyGenerator.Sequence seq = dialect.getKeyGenerator();

            Insert insertQuery = new Insert(
                    queryName,
                    Lists.add(parameters,
                            new Parameter.SqlLiteralParameter(primaryKeyFieldName, seq.nextFromSequenceEmbedded(sequenceName))),
                    tableName);

            execute(insertQuery);
        }
        else {
            Insert insertQuery = new Insert(queryName, parameters, tableName);
            execute(insertQuery);
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

    public class QueryBuilder<T extends QueryBuilder>  {
        final String queryName;
        final List<Parameter> parameters = new ArrayList<>();

        QueryBuilder(String queryName) {
            this.queryName = queryName;
        }

        public T value(String name, String value) {
            parameters.add(new Parameter.StringParameter(name, value));
            return (T)this;
        }

        public T value(String name, Integer value) {
            parameters.add(new Parameter.IntParameter(name, value));
            return (T)this;
        }

        public T value(String name, LocalDateTime value) {
            parameters.add(new Parameter.LocalDateTimeParameter(name, value));
            return (T)this;
        }

        public T value(String name, BigDecimal value) {
            parameters.add(new Parameter.BigDecimalParameter(name, value));
            return (T)this;
        }

        public T value(String name, Long value) {
            parameters.add(new Parameter.LongParameter(name, value));
            return (T)this;
        }
    }

    public class InsertBuilder extends QueryBuilder<InsertBuilder> {
        private String tableName;
        private String primaryKeyFieldName;
        private String sequenceName;

        InsertBuilder(String queryName) {
            super(queryName);
        }

        public InsertBuilder into(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public InsertBuilder sequence(String primaryKeyFieldName, String sequenceName) {
            this.primaryKeyFieldName = primaryKeyFieldName;
            this.sequenceName = sequenceName;
            return this;
        }

        public long executeAndGetSequence()  {
            return insertAndGetSequence(queryName, Collections.unmodifiableList(parameters), tableName, primaryKeyFieldName, sequenceName);
        }

        public void execute()  {
            insert(queryName, Collections.unmodifiableList(parameters), tableName, primaryKeyFieldName, sequenceName);
        }
    }
}
