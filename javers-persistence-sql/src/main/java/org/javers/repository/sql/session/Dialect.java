package org.javers.repository.sql.session;

import org.javers.common.validation.Validate;
import org.javers.repository.sql.DialectName;

abstract class Dialect {
    private final DialectName dialectName;

    Dialect(DialectName dialectName) {
        Validate.argumentIsNotNull(dialectName);
        this.dialectName = dialectName;
    }

    boolean supportsSequences() {
        return getKeyGenerator() instanceof KeyGenerator.Sequence;
    }

    abstract <T extends KeyGenerator> T getKeyGenerator();

    public DialectName getName() {
        return dialectName;
    }
}
