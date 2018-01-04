package org.javers.repository.sql;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;

/**
 * Proper JDBC driver .jar should be provided on the classpath
 *
 * @author bartosz walacik
 */
public enum DialectName {
    H2,
    POSTGRES,
    ORACLE,
    MYSQL,

    /** Microsoft SQL Server*/
    MSSQL,

    /** incubating */
    DB2,

    /** incubating */
    DB2400;

    public DialectRegistry getPolyDialectName() {
        return DialectRegistry.valueOf(this.name());
    }

    public Dialect getPolyDialect() {
        return getPolyDialectName().getDialect();
    }
}
