package org.javers.repository.sql;

import org.polyjdbc.core.dialect.*;

/**
 * @author bartosz walacik
 */
public enum DialectName {
    H2("org.h2.Driver"),
    POSTGRES( "org.postgresql.Driver"),
    MYSQL("com.mysql.jdbc.Driver");

    private String driverClass;

    private DialectName(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public DialectRegistry getPolyDialectName() {
        return DialectRegistry.valueOf(this.name());
    }

    public Dialect getPolyDialect() {
        return getPolyDialectName().getDialect();
    }
}
