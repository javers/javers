package org.javers.repository.sql.session;

import org.javers.common.validation.Validate;
import org.javers.repository.sql.DialectName;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Session implements AutoCloseable{
    private final DialectName dialectName;
    private final Map<String, PreparedStatement> preparedStatements = new HashMap<>();

    Session(DialectName dialectName) {
        this.dialectName = dialectName;
    }

    public InsertQuery createInsert(String queryName, List<Query.Parameter> values, String tableName, String primaryKeyFieldName, String sequenceName) {
        Validate.argumentsAreNotNull(queryName, values, tableName, primaryKeyFieldName, sequenceName);
        if (dialectName.getPolyDialect().supportsSequences()) {
            return new InsertQuery(queryName, Collections.emptyList(), tableName, primaryKeyFieldName, sequenceName);
        }
        else {
            return new InsertQuery(queryName, Collections.emptyList(), tableName);
        }
    }

    @Override
    public void close() {
        try {
            for(PreparedStatement p : preparedStatements.values()) {
                p.close();
            }
        } catch (SQLException e) {
            throw new SqlUncheckedException("error while closing PreparedStatement", e);
        }
    }

}
