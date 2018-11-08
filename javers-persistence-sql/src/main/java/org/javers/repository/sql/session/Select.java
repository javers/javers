package org.javers.repository.sql.session;

import java.util.Collections;
import java.util.List;

class Select extends Query {

    Select(String name, String rawSQL) {
        super(name, Collections.emptyList(), rawSQL);
    }

    Select(String name, List<Parameter> params, String rawSQL) {
        super(name, params, rawSQL);
    }
}
