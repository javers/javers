package org.javers.repository.sql.session;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.schema.TableNameProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Comparator.*;
import static org.javers.repository.sql.session.Parameter.longParam;
import static org.javers.repository.sql.session.Parameter.stringParam;

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

    Session(DialectName dialectName, ConnectionProvider connectionProvider, String sessionName) {
        this.dialect = Dialects.fromName(dialectName);
        this.connectionProvider = connectionProvider;
        this.sessionName = sessionName;
    }

    public SelectBuilder select(String selectClauseSQL) {
        return new SelectBuilder(selectClauseSQL);
    }

    public InsertBuilder insert(String queryName) {
        return new InsertBuilder(queryName);
    }

    private long executeInsertAndGetSequence(String queryName, List<Parameter> parameters, String tableName, String primaryKeyFieldName, String sequenceName) {
        Validate.argumentsAreNotNull(queryName, parameters, tableName, primaryKeyFieldName, sequenceName);

        if (dialect.supportsSequences()) {
            KeyGenerator.Sequence seq = dialect.getKeyGenerator();
            long newId = executeQueryForLong(new Select("SELECT next from seq "+ sequenceName, seq.nextFromSequenceAsSelect(sequenceName)));

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
            return executeQueryForLong(new Select("last autoincrement id", autoincrement.lastInsertedAutoincrement()));
        }
    }

    private void executeInsert(String queryName, List<Parameter> parameters, String tableName, String primaryKeyFieldName, String sequenceName) {
        Validate.argumentsAreNotNull(queryName, parameters, tableName);

        if (dialect.supportsSequences() && sequenceName != null) {
            KeyGenerator.Sequence seq = dialect.getKeyGenerator();

            Insert insertQuery = new Insert(
                    queryName,
                    Lists.add(parameters, new Parameter.SqlLiteralParameter(primaryKeyFieldName, seq.nextFromSequenceEmbedded(sequenceName))),
                    tableName);

            execute(insertQuery);
        }
        else {
            Insert insertQuery = new Insert(queryName, parameters, tableName);
            execute(insertQuery);
        }
    }

    private long executeQueryForLong(Select select) {
        PreparedStatementExecutor executor = getOrCreatePreparedStatement(select);
        return executor.executeQueryForLong(select);
    }

    private Optional<BigDecimal> executeQueryForOptionalBigDecimal(Select select) {
        PreparedStatementExecutor executor = getOrCreatePreparedStatement(select);
        return executor.executeQueryForOptionalBigDecimal(select);
    }

    private Optional<Long> executeQueryForOptionalLong(Select select) {
        PreparedStatementExecutor executor = getOrCreatePreparedStatement(select);
        return executor.executeQueryForOptionalLong(select);
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
        logger.debug("SQL session '" + sessionName + "' finished. {} statement(s) executed in {} millis.",
                statementExecutors.values().stream().mapToInt(i -> i.getExecutionCount()).sum(),
                statementExecutors.values().stream().mapToLong(i -> i.getExecutionTotalMillis()).sum());

        List<PreparedStatementExecutor> executors = new ArrayList<>(statementExecutors.values());
        Collections.sort(executors, (e1, e2) -> e2.getExecutionTotalMillis() > e1.getExecutionTotalMillis() ? 1 : -1);

        executors.forEach(e -> logger.debug("* "+e.printStats()));
    }

    public class QueryBuilder<T extends QueryBuilder>  {
        private String queryName;
        final List<Parameter> parameters = new ArrayList<>();

        public T queryName(String queryName) {
            this.queryName = queryName;
            return (T)this;
        }

        public List<Parameter> getParameters() {
            return Collections.unmodifiableList(parameters);
        }

        public String getQueryName() {
            return queryName;
        }
    }

    public class SelectBuilder extends QueryBuilder<SelectBuilder> {
        private String rawSql;

        SelectBuilder(String selectClauseSQL) {
            rawSql = "SELECT " + selectClauseSQL;
        }

        public SelectBuilder sql(String sql) {
            this.rawSql = sql;
            return this;
        }

        public SelectBuilder from(String fromClauseSQL) {
            rawSql += " FROM " + fromClauseSQL + " WHERE 1 = 1";
            return this;
        }

        public SelectBuilder and(String columnName, String operator, Parameter parameter) {
            parameters.add(parameter);
            rawSql += " AND " + columnName + " " + operator + " ?";
            return this;
        }

        public SelectBuilder and(String predicateSQL, Parameter... params) {
            parameters.addAll(Lists.immutableListOf(params));
            rawSql += " AND " + predicateSQL;
            return this;
        }

        public SelectBuilder and(String columnName, Long value) {
            return and(columnName, "=", longParam(value));
        }

        public SelectBuilder and(String columnName, String value) {
            return and(columnName, "=", stringParam(value));
        }

        public SelectBuilder and(String columnName, BigDecimal value) {
            return and(columnName, "=", Parameter.bigDecimalParam(value));
        }

        private Select build() {
            return new Select("SELECT "+ getQueryName(), getParameters(), rawSql);
        }

        public SelectBuilder limit(int limit, int offset) {

            return this;
        }

        public long queryForLong(String queryName) {
            queryName(queryName);
            return executeQueryForLong(build());
        }

        public Optional<BigDecimal> queryForOptionalBigDecimal(String queryName) {
            queryName(queryName);
            return executeQueryForOptionalBigDecimal(build());
        }

        public Optional<Long> queryForOptionalLong() {
            return executeQueryForOptionalLong(build());
        }
    }

    public class InsertBuilder extends QueryBuilder<InsertBuilder> {
        private String tableName;
        private String primaryKeyFieldName;
        private String sequenceName;

        InsertBuilder(String queryName) {
            queryName("INSERT "+ queryName);
        }

        public InsertBuilder into(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public InsertBuilder value(String name, String value) {
            parameters.add(new Parameter.StringParameter(name, value));
            return this;
        }

        public InsertBuilder value(String name, Integer value) {
            parameters.add(new Parameter.IntParameter(name, value));
            return this;
        }

        public InsertBuilder value(String name, LocalDateTime value) {
            parameters.add(new Parameter.LocalDateTimeParameter(name, value));
            return this;
        }

        public InsertBuilder value(String name, BigDecimal value) {
            parameters.add(new Parameter.BigDecimalParameter(name, value));
            return this;
        }

        public InsertBuilder value(String name, Long value) {
            parameters.add(new Parameter.LongParameter(name, value));
            return this;
        }

        public InsertBuilder sequence(String primaryKeyFieldName, String sequenceName) {
            this.primaryKeyFieldName = primaryKeyFieldName;
            this.sequenceName = sequenceName;
            return this;
        }

        public long executeAndGetSequence()  {
            return executeInsertAndGetSequence(getQueryName(), getParameters(), tableName, primaryKeyFieldName, sequenceName);
        }

        public void execute()  {
            executeInsert(getQueryName(), getParameters(), tableName, primaryKeyFieldName, sequenceName);
        }
    }
}
