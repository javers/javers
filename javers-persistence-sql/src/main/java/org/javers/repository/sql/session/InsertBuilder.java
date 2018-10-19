package org.javers.repository.sql.session;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InsertBuilder extends QueryBuilder<InsertBuilder> {
    private Session session;
    private String tableName;
    private String primaryKeyFieldName;
    private String sequenceName;

    InsertBuilder(Session session, String queryName) {
        this.session = session;
        queryName("INSERT " + queryName);
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

    public long executeAndGetSequence() {
        return session.executeInsertAndGetSequence(getQueryName(), getParameters(), tableName, primaryKeyFieldName, sequenceName);
    }

    public void execute() {
        session.executeInsert(getQueryName(), getParameters(), tableName, primaryKeyFieldName, sequenceName);
    }
}
