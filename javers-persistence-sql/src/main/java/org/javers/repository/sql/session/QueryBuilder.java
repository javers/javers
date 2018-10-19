package org.javers.repository.sql.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class QueryBuilder<T extends QueryBuilder> {
    private String queryName;
    final List<Parameter> parameters = new ArrayList<>();

    public T queryName(String queryName) {
        this.queryName = queryName;
        return (T) this;
    }

    public List<Parameter> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public String getQueryName() {
        return queryName;
    }
}
