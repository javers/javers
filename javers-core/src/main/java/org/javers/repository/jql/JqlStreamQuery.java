package org.javers.repository.jql;

import org.javers.common.validation.Validate;

import java.util.ArrayList;
import java.util.List;

//TODO deprecated?
class JqlStreamQuery {
    private final List<JqlQuery> queries = new ArrayList<>();

    JqlStreamQuery(JqlQuery initialQuery) {
        Validate.argumentIsNotNull(initialQuery);
        queries.add(initialQuery);
    }
}
