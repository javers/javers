package org.javers.repository.sql.session;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.polyjdbc.core.type.ColumnTypeMapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    Query(String name, List<Parameter> params, String rawSQL) {
        this.name = name;
        this.rawSQL = rawSQL;
        this.orderedParameters = params;
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

    private Parameter findParameter(String name, List<Parameter> params) {
        return params.stream().filter(it -> it.getName().equals(name)).findFirst().orElseThrow(
                () -> new JaversException(JaversExceptionCode.RUNTIME_EXCEPTION, "parameter '"+name+"' not found in raw SQL '"+rawSQL+"'")
        );
    }
}
