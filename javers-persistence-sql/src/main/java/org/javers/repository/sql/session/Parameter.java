package org.javers.repository.sql.session;

import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public abstract class Parameter<T> {
    /** nullable */
    private final String name;
    private final T value;

    Parameter(String name, T value) {
        this.name = name;
        this.value = value;
    }

    abstract void injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException;

    public static Parameter<Long> longParam(Long value){
        return new LongParameter(null, value);
    }

    public static Parameter<String> stringParam(String value){
        return new StringParameter(null, value);
    }

    public static Parameter<BigDecimal> bigDecimalParam(BigDecimal value){
        return new BigDecimalParameter(null, value);
    }

    public static Parameter<LocalDateTime> localDateTimeParam(LocalDateTime value){
        return new LocalDateTimeParameter(null, value);
    }

    String getName() {
        return name;
    }

    T getValue() {
        return value;
    }

    String getRawSqlRepresentation() {
        return "?";
    }

    static class StringParameter extends Parameter<String> {
        StringParameter(String name, String value) {
            super(name, value);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setString(order, getValue());
        }
    }

    static class LongParameter extends Parameter<Long> {
        LongParameter(String name, Long value) {
            super(name, value);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setLong(order, getValue());
        }
    }

    static class InlinedParameter extends Parameter<String> {
        InlinedParameter(String name, String inlinedExpression) {
            super(name, inlinedExpression);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            return;
        }

        @Override
        String getRawSqlRepresentation() {
            return getValue();
        }
    }

    static class IntParameter extends Parameter<Integer> {
        IntParameter(String name, Integer value) {
            super(name, value);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setInt(order, getValue());
        }
    }

    static class BigDecimalParameter extends Parameter<BigDecimal> {
        BigDecimalParameter(String name, BigDecimal value) {
            super(name, value);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setBigDecimal(order, getValue());
        }
    }

    static class LocalDateTimeParameter extends Parameter<LocalDateTime> {
        LocalDateTimeParameter(String name, LocalDateTime value) {
            super(name, value);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setTimestamp(order, toTimestamp(getValue()));
        }

        private Timestamp toTimestamp(LocalDateTime value) {
            return new Timestamp(UtilTypeCoreAdapters.toUtilDate(value).getTime());
        }
    }

    static class SqlLiteralParameter extends Parameter<String> {
        SqlLiteralParameter(String name, String value) {
            super(name, value);
        }

        @Override
        String getRawSqlRepresentation() {
            return getValue();
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
        }
    }
}
