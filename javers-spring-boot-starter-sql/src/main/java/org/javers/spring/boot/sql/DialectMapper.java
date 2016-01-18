package org.javers.spring.boot.sql;

import org.javers.common.collections.Function;
import org.javers.repository.sql.DialectName;
import org.springframework.orm.jpa.vendor.Database;

import java.util.HashMap;
import java.util.Map;

public class DialectMapper implements Function<Database, DialectName> {

    private static final Map<Database, DialectName> mapping = new HashMap() {
        {
            put(Database.DEFAULT, DialectName.H2);
            put(Database.H2, DialectName.H2);
            put(Database.SQL_SERVER, DialectName.MSSQL);
            put(Database.ORACLE, DialectName.ORACLE);
            put(Database.MYSQL, DialectName.MYSQL);
            put(Database.POSTGRESQL, DialectName.POSTGRES);
        }
    };

    @Override
    public DialectName apply(Database hibernateDialectName) {
        return mapping.get(hibernateDialectName);
    }
}
