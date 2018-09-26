package org.javers.repository.sql.session;

import org.javers.common.validation.Validate;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

abstract class Parameter<T> {
    private final String name;
    private final T value;

    Parameter(String name, T value) {
        Validate.argumentIsNotNull(name);
        this.name = name;
        this.value = value;
    }

    abstract void injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException;

    String getName() {
        return name;
    }

    T getValue() {
        return value;
    }

    public String getRawSqlRepresentation() {
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
}
