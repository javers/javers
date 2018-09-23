package org.javers.repository.sql.session;

import java.util.List;
import java.util.stream.Collectors;

class Insert extends Query {

    Insert(String queryName,
           List<Parameter> parameters,
           String tableName) {
        super(queryName, parameters, buildSql(parameters, tableName));
    }

    static private String buildSql(List<Parameter> parameters, String tableName) {

        String fieldNames = parameters.stream()
                .map(it -> it.getName())
                .collect(Collectors.joining(", "));

        String valuePlaceholders = parameters.stream()
                .map(it -> it.getRawSqlRepresentation())
                .collect(Collectors.joining(","));

        return "INSERT INTO " + tableName +
                " ( " + fieldNames + " ) VALUES " +
                " ( " + valuePlaceholders + " )";
    }
}
