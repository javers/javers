package org.javers.repository.sql.session;

import org.javers.common.string.ToStringBuilder;

import java.util.List;
import java.util.stream.Collectors;

class InsertQuery extends Query {

    InsertQuery(String queryName,
                List<Parameter> parameters,
                String tableName,
                String primaryKeyFieldName,
                String sequenceName) {
        this(queryName, parameters, tableName, new RegularSequenceDef(primaryKeyFieldName, sequenceName));
    }

    InsertQuery(String queryName,
                List<Parameter> parameters,
                String tableName) {
        this(queryName, parameters, tableName, AutoincrementSequenceDef.INSTANCE);
    }

    private InsertQuery(String queryName, List<Parameter> parameters, String tableName, SequenceDefinition sequenceDefinition) {
        super(queryName, parameters, buildSql(parameters, tableName, sequenceDefinition));
    }

    static private String buildSql(List<Parameter> parameters, String tableName, SequenceDefinition sequenceDefinition) {
        String fieldNames = ToStringBuilder.join(parameters.stream()
                .map(it -> it.getName())
                .collect(Collectors.toList()));

        String valuePlaceholders = ToStringBuilder.join(parameters.stream()
                .map(it -> "$"+it.getName())
                .collect(Collectors.toList()));

        return "INSERT INTO " + tableName +
                " ( " + fieldNames + " ) VALUES " +
                " ( " + valuePlaceholders + " )";
    }

    static class SequenceDefinition {
    }

    static class AutoincrementSequenceDef extends SequenceDefinition {
        static final AutoincrementSequenceDef INSTANCE = new AutoincrementSequenceDef();
    }

    static class RegularSequenceDef extends SequenceDefinition {
        private final String primaryKeyFieldName;
        private final String sequenceName;

        RegularSequenceDef(String primaryKeyFieldName, String sequenceName) {
            this.primaryKeyFieldName = primaryKeyFieldName;
            this.sequenceName = sequenceName;
        }
    }
}
