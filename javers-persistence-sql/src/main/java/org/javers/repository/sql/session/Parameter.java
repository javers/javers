package org.javers.repository.sql.session;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

public abstract class Parameter<T> {
    /** nullable */
    private final String name;
    private final T value;

    Parameter(String name, T value) {
        this.name = name;
        this.value = value;
    }

    abstract void injectValuesTo(PreparedStatement preparedStatement, AtomicInteger order) throws SQLException;

    public static Parameter<Long> longParam(Long value){
        return new LongParameter(null, value);
    }

    public static Parameter<String> stringParam(String value){
        return new StringParameter(null, value);
    }

    public static Parameter<Collection<String>> listParam(Collection<String> value){
        return new ListParameter(null, value);
    }

    public static Parameter<BigDecimal> bigDecimalParam(BigDecimal value){
        return new BigDecimalParameter(null, value);
    }

    public static Parameter<LocalDateTime> localDateTimeParam(LocalDateTime value){
        return new LocalDateTimeParameter(null, value);
    }

    public static Parameter<Instant> instantParam(Instant value) {
        return new InstantParameter(null, value);
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
        void injectValuesTo(PreparedStatement preparedStatement, AtomicInteger order) throws SQLException {
            preparedStatement.setString(order.getAndIncrement(), getValue());
        }
    }

    static class ListParameter extends Parameter<Collection<String>> {
        ListParameter(String name, Collection<String> value) {
            super(name, value);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, AtomicInteger order) throws SQLException {
            for (String val : getValue()) {
                preparedStatement.setString(order.getAndIncrement(), val);
            }

        }
    }

    static class LongParameter extends Parameter<Long> {
        LongParameter(String name, Long value) {
            super(name, value);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, AtomicInteger order) throws SQLException {
            preparedStatement.setLong(order.getAndIncrement(), getValue());
        }
    }

    static class InlinedParameter extends Parameter<String> {
        InlinedParameter(String name, String inlinedExpression) {
            super(name, inlinedExpression);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, AtomicInteger order) throws SQLException {
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
        void injectValuesTo(PreparedStatement preparedStatement, AtomicInteger order) throws SQLException {
            preparedStatement.setInt(order.getAndIncrement(), getValue());
        }
    }

    static class BigDecimalParameter extends Parameter<BigDecimal> {
        BigDecimalParameter(String name, BigDecimal value) {
            super(name, value);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, AtomicInteger order) throws SQLException {
            preparedStatement.setBigDecimal(order.getAndIncrement(), getValue());
        }
    }

    static class LocalDateTimeParameter extends Parameter<LocalDateTime> {
        LocalDateTimeParameter(String name, LocalDateTime value) {
            super(name, value);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, AtomicInteger order) throws SQLException {
            preparedStatement.setTimestamp(order.getAndIncrement(), toTimestamp(getValue()));
        }

        private Timestamp toTimestamp(LocalDateTime value) {
            return new Timestamp(UtilTypeCoreAdapters.toUtilDate(value).getTime());
        }
    }

    static class InstantParameter extends Parameter<Instant> {
        InstantParameter(String name, Instant value) {
            super(name, value);
        }

        @Override
        void injectValuesTo(PreparedStatement preparedStatement, AtomicInteger order) throws SQLException {
            preparedStatement.setString(order.getAndIncrement(), getValue().toString());
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
        void injectValuesTo(PreparedStatement preparedStatement, AtomicInteger order) throws SQLException {
        }
    }
}
