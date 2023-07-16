package org.javers.spring.boot.sql;

import org.hibernate.dialect.*;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.repository.sql.DialectName;

public class DialectMapper {

    public DialectName map(Dialect hibernateDialect) {

        if (hibernateDialect instanceof SQLServerDialect) {
            return DialectName.MSSQL;
        }
        if (hibernateDialect instanceof H2Dialect){
            return DialectName.H2;
        }
        if (hibernateDialect instanceof OracleDialect){
            return DialectName.ORACLE;
        }
        if (hibernateDialect instanceof PostgreSQLDialect){
            return DialectName.POSTGRES;
        }
        if (hibernateDialect instanceof MySQLDialect){
            return DialectName.MYSQL;
        }
        throw new JaversException(JaversExceptionCode.UNSUPPORTED_SQL_DIALECT, hibernateDialect.getClass().getSimpleName());
    }
}
