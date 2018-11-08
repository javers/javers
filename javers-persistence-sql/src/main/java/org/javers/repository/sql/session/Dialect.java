package org.javers.repository.sql.session;

import org.javers.common.validation.Validate;
import org.javers.repository.sql.DialectName;

import static org.javers.repository.sql.session.Parameter.longParam;

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

    DialectName getName() {
        return dialectName;
    }

    void limit(SelectBuilder query, long limit, long offset) {
        query.append("LIMIT ? OFFSET ?", longParam(limit), longParam(offset));
    }
}
