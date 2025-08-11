package org.javers.repository.sql.session;

public interface JsonColumnSupportDialect {

    default JsonCastingExpression jsonCastingExpression() {
        return param -> param;
    }
}
