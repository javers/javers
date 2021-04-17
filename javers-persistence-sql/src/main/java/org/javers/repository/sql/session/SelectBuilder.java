package org.javers.repository.sql.session;

import org.javers.common.collections.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.javers.repository.sql.session.Parameter.longParam;
import static org.javers.repository.sql.session.Parameter.stringParam;

public class SelectBuilder extends QueryBuilder<SelectBuilder> {
    private Session session;
    private String rawSql;

    SelectBuilder(Session session, String selectClauseSQL) {
        this.session = session;
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
        return new Select("SELECT " + getQueryName(), getParameters(), rawSql);
    }

    public SelectBuilder limit(int limit, int offset) {
        session.getDialect().limit(this, limit, offset);
        return this;
    }

    public SelectBuilder orderByDesc(String columns) {
        this.append("ORDER BY " + columns + " DESC");
        return this;
    }

    public SelectBuilder orderByAsc(String columns) {
        this.append("ORDER BY " + columns + " ASC");
        return this;
    }

    public SelectBuilder wrap(String leftSql, String rightSql, Parameter... params) {
        parameters.addAll(Lists.immutableListOf(params));
        rawSql = leftSql + " " + rawSql + " " + rightSql;
        return this;
    }

    public SelectBuilder append(String moreSql, Parameter... params) {
        return wrap("", moreSql, params);
    }

    public long queryForLong(String queryName) {
        queryName(queryName);
        return session.executeQueryForLong(build());
    }

    public Optional<BigDecimal> queryForOptionalBigDecimal(String queryName) {
        queryName(queryName);
        return session.executeQueryForOptionalBigDecimal(build());
    }

    public Optional<Long> queryForOptionalLong() {
        return session.executeQueryForOptionalLong(build());
    }

    public <T> List<T> executeQuery(ObjectMapper<T> objectMapper) {
        return session.executeQuery(build(), objectMapper);
    }
}
