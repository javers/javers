package org.javers.repository.sql.session;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.repository.sql.DialectName;

class Dialect {
    private final DialectName dialectName;

    private Dialect(DialectName dialectName) {
        Validate.argumentIsNotNull(dialectName);
        this.dialectName = dialectName;
    }

    static Dialect fromName(DialectName dialectName) {
        if (DialectName.H2 == dialectName) {
            return new H2(dialectName);
        }
        if (DialectName.MYSQL == dialectName) {
            return new MysqlDialect(dialectName);
        }
        return new Dialect(dialectName);
    }

    boolean supportsSequences() {
        return true;
    }

    String lastInsertedAutoincrement() {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);

    }

    public DialectName getName() {
        return dialectName;
    }

    String nextFromSequence(String seqName) {
        return "SELECT " + seqName + ".nextval";
    }

    static class H2 extends Dialect {
        H2(DialectName dialectName) {
            super(dialectName);
        }
    }

    static class MysqlDialect extends Dialect {
        MysqlDialect(DialectName dialectName) {
            super(dialectName);
        }

        @Override
        boolean supportsSequences() {
            return false;
        }

        @Override
        String lastInsertedAutoincrement() {
            return "select last_insert_id()";
        }
    }
}
