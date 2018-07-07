package org.javers.repository.sql.session;

import org.javers.common.string.ToStringBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class InsertQuery extends Query {
    private final String queryName;
    private final String tableName;
    private final List<Parameter> values;
    private final SequenceDefinition sequenceDefinition;

    InsertQuery(String queryName,
                List<Parameter> values,
                String tableName,
                String primaryKeyFieldName,
                String sequenceName) {
        this(queryName, values, tableName, new RegularSequenceDef(primaryKeyFieldName, sequenceName));
    }

    InsertQuery(String queryName,
                List<Parameter> values,
                String tableName) {
        this(queryName, values, tableName, AutoincrementSequenceDef.INSTANCE);
    }

    private InsertQuery(String queryName, List<Parameter> values, String tableName, SequenceDefinition sequenceDefinition) {
        this.queryName = queryName;
        this.tableName = tableName;
        this.values = values;
        this.sequenceDefinition = sequenceDefinition;
    }

    String generateSql() {
        String fieldNames = ToStringBuilder.join(values.stream()
                .map(it -> it.getName())
                .collect(Collectors.toList()));

        String valuePlaceholders = ToStringBuilder.join(values.stream()
                .map(it -> ":"+it.getName())
                .collect(Collectors.toList()));


        return "INSERT INTO " + tableName +
                " ( " + fieldNames + " ) VALUES " +
                " ( " + valuePlaceholders + " )";
    }

    String getName() {
        return queryName;
    }

    static class SequenceDefinition {

    }

    static class AutoincrementSequenceDef extends SequenceDefinition {
        public static final AutoincrementSequenceDef INSTANCE = new AutoincrementSequenceDef();
    }

    static class RegularSequenceDef extends SequenceDefinition {
        private final String primaryKeyFieldName;
        private final String sequenceName;

        public RegularSequenceDef(String primaryKeyFieldName, String sequenceName) {
            this.primaryKeyFieldName = primaryKeyFieldName;
            this.sequenceName = sequenceName;
        }
    }
}
