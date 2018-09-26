package org.javers.repository.sql.session;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.repository.sql.DialectName;

class Dialects {

    static Dialect fromName(DialectName dialectName) {
        if (DialectName.H2 == dialectName) {
            return new H2(dialectName);
        }
        if (DialectName.MYSQL == dialectName) {
            return new MysqlDialect(dialectName);
        }
        if (DialectName.POSTGRES == dialectName) {
            return new PostgresDialect(dialectName);
        }
        if (DialectName.ORACLE == dialectName) {
            return new OracleDialect(dialectName);
        }
        if (DialectName.MSSQL == dialectName) {
            return new MsSqlDialect(dialectName);
        }
        throw new JaversException(JaversExceptionCode.UNSUPPORTED_SQL_DIALECT, dialectName);
    }

    static class H2 extends Dialect {
        H2(DialectName dialectName) {
            super(dialectName);
        }

        @Override
        KeyGenerator getKeyGenerator() {
            return new KeyGenerator.Sequence() {
                public String nextFromSequenceAsSelect(String seqName) {
                    return "SELECT " + nextFromSequenceEmbedded(seqName);
                }

                public String nextFromSequenceEmbedded(String seqName) {
                    return seqName + ".nextval";
                }
            };
        }
    }

    static class MysqlDialect extends Dialect {
        MysqlDialect(DialectName dialectName) {
            super(dialectName);
        }

        @Override
        KeyGenerator getKeyGenerator() {
            return (KeyGenerator.Autoincrement) () -> "select last_insert_id()";
        }
    }

    static class MsSqlDialect extends Dialect {
        MsSqlDialect(DialectName dialectName) {
            super(dialectName);
        }

        @Override
        KeyGenerator getKeyGenerator() {
            return new KeyGenerator.Sequence() {
                public String nextFromSequenceAsSelect(String seqName) {
                    return "SELECT + " + nextFromSequenceEmbedded(seqName);
                }

                public String nextFromSequenceEmbedded(String seqName) {
                    return "NEXT VALUE FOR "+seqName;
                }
            };
        }
    }

    static class PostgresDialect extends Dialect {
        PostgresDialect(DialectName dialectName) {
            super(dialectName);
        }

        @Override
        KeyGenerator getKeyGenerator() {
            return new KeyGenerator.Sequence() {
                public String nextFromSequenceAsSelect(String seqName) {
                    return "SELECT "+ nextFromSequenceEmbedded(seqName);
                }

                public String nextFromSequenceEmbedded(String seqName) {
                    return "nextval('" + seqName + "')";
                }
            };
        }
    }

    static class OracleDialect extends Dialect {
        OracleDialect(DialectName dialectName) {
            super(dialectName);
        }

        @Override
        KeyGenerator getKeyGenerator() {
            return new KeyGenerator.Sequence() {
                public String nextFromSequenceAsSelect(String seqName) {
                    return "SELECT "+ nextFromSequenceEmbedded(seqName) +" from dual";
                }

                public String nextFromSequenceEmbedded(String seqName) {
                    return seqName + ".nextval";
                }
            };
        }
    }
}
