package org.javers.repository.sql.session;

import java.util.List;

// TODO
class FastInsert extends Insert {

    FastInsert(String queryName, List<Parameter> parameters, String tableName) {
        super(queryName, parameters, tableName);
    }
}
