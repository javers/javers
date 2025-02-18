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

    abstract int injectValuesTo(PreparedStatement preparedStatement, int orderStart) throws SQLException;

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
        int injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setString(order, getValue());
            return order + 1;
        }
    }

    static class JsonParameter extends Parameter<String> {

        private final JsonCastingExpression jsonCastingExpression;

        JsonParameter(String name, String value, JsonCastingExpression jsonCastingExpression) {
            super(name, value);
            this.jsonCastingExpression = jsonCastingExpression;
        }

        @Override
        String getRawSqlRepresentation() {
            return jsonCastingExpression.castToJson(super.getRawSqlRepresentation());
        }

        @Override
        int injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setString(order, getValue());
            return order + 1;
        }
    }

    static class ListParameter extends Parameter<Collection<String>> {
        ListParameter(String name, Collection<String> value) {
            super(name, value);
        }

        @Override
        int injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            int k = order;
            for (String val : getValue()) {
                preparedStatement.setString(k++, val);
            }
            return k;
        }
    }

    static class LongParameter extends Parameter<Long> {
        LongParameter(String name, Long value) {
            super(name, value);
        }

        @Override
        int injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setLong(order, getValue());
            return order + 1;
        }
    }

    static class InlinedParameter extends Parameter<String> {
        InlinedParameter(String name, String inlinedExpression) {
            super(name, inlinedExpression);
        }

        @Override
        int injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            return order;
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
        int injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setInt(order, getValue());
            return order + 1;
        }
    }

    static class BigDecimalParameter extends Parameter<BigDecimal> {
        BigDecimalParameter(String name, BigDecimal value) {
            super(name, value);
        }

        @Override
        int injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setBigDecimal(order, getValue());
            return order + 1;
        }
    }

    static class LocalDateTimeParameter extends Parameter<LocalDateTime> {
        LocalDateTimeParameter(String name, LocalDateTime value) {
            super(name, value);
        }

        @Override
        int injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setTimestamp(order, toTimestamp(getValue()));
            return order + 1;
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
        int injectValuesTo(PreparedStatement preparedStatement, int order) throws SQLException {
            preparedStatement.setString(order, getValue().toString());
            return order + 1;
        }
    }
}
