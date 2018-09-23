package org.javers.repository.sql.session;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.polyjdbc.core.type.ColumnTypeMapper;
import org.polyjdbc.core.type.SqlType;
import org.polyjdbc.core.type.TypeWrapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author bartosz.walacik
 */
abstract class Query {
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\$[A-Za-z0-9_-]*");
    private static ColumnTypeMapper columnTypeMapper = new ColumnTypeMapper();

    private final String name;
    private final List<Parameter> orderedParameters;
    private final String rawSQL;
  //  private final String sqlWithPlaceholders;

    Query(String name, List<Parameter> params, String rawSQL) {
        this.name = name;
        this.rawSQL = rawSQL;
        this.orderedParameters = params;

        //TODO wtf?
        /*
        this.orderedParameters = new ArrayList<>();
        Matcher matcher = ARGUMENT_PATTERN.matcher(this.rawSQL);
        while (matcher.find()) {
            String foundPattern = matcher.group();
            String paramName = foundPattern.substring(1);
            orderedParameters.add(findParameter(paramName, params));

            //replacement = createReplacement(arguments.get(argumentName));
            String replacement = "?";
            tmpSql = tmpSql.replaceFirst(foundPattern, replacement);
        }
        */
    }

    String rawSQl() {
        return rawSQL;
    };

    String name() {
        return name;
    }

    void injectValuesTo(PreparedStatement preparedStatement) throws SQLException {
        int parameterNumber = 1;
        for (Parameter parameter : orderedParameters) {
            parameter.injectValuesTo(preparedStatement,parameterNumber);
            parameterNumber++;
        }
    }

    @Deprecated
    //TODO wtf?
    private void injectValue(PreparedStatement preparedStatement, int parameterNumber, Object value) throws SQLException {
        if (value != null) {
            SqlType type = columnTypeMapper.forClass(value.getClass());
            Object injectedValue = value;
            if (value instanceof TypeWrapper) {
                injectedValue = ((TypeWrapper) value).value();
            } else
            if (value instanceof java.util.Date) {
                //Oracle is unhappy with java.util.Date and insists on java.sql.Date
                injectedValue = new java.sql.Date(((java.util.Date)value).getTime());
            } else
            if (value instanceof Character){
                //Oracle really dislike Java char type
                injectedValue = String.valueOf(value);
            } else
            if (value instanceof Boolean){
                //Oracle. Why U No Boolean?
                //surprisingly preparedStatement.setBoolean(,) works with Oracle and translates boolean to 0/1
                //but preparedStatement.setObject(,,BOOLEAN) doesn't
                preparedStatement.setBoolean(parameterNumber, (Boolean)value);
                return;
            }

            preparedStatement.setObject(parameterNumber, injectedValue, type.code());
        } else {
            preparedStatement.setObject(parameterNumber, null);
        }
    }

    private Parameter findParameter(String name, List<Parameter> params) {
        return params.stream().filter(it -> it.getName().equals(name)).findFirst().orElseThrow(
                () -> new JaversException(JaversExceptionCode.RUNTIME_EXCEPTION, "parameter '"+name+"' not found in raw SQL '"+rawSQL+"'")
        );
    }
}
