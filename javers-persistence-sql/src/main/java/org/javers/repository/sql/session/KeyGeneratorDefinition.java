package org.javers.repository.sql.session;

import org.javers.repository.sql.KeyGenerator;

interface KeyGeneratorDefinition {

    KeyGenerator createKeyGenerator();

    interface SequenceDefinition extends KeyGeneratorDefinition {
        String nextFromSequenceAsSQLExpression(String seqName);

        default String nextFromSequenceAsSelect(String seqName) {
            return "SELECT " + nextFromSequenceAsSQLExpression(seqName);
        }

        @Override
        default KeyGenerator createKeyGenerator() {
            return new SequenceAllocation(this);
        }
    }

    interface AutoincrementDefinition extends KeyGeneratorDefinition {
        String lastInsertedAutoincrement();

        @Override
        default KeyGenerator createKeyGenerator() {
            return new AutoincrementGenerator(this);
        }
    }
}
