package org.javers.repository.sql.session;

interface KeyGeneratorDefinition {

    KeyGenerator createKeyGenerator();

    interface SequenceDefinition extends KeyGeneratorDefinition {
        String nextFromSequenceAsSelect(String seqName);

        @Override
        default KeyGenerator createKeyGenerator() {
            return new KeyGenerator.SequenceAllocation(this);
        }
    }

    interface AutoincrementDefinition extends KeyGeneratorDefinition {
        String lastInsertedAutoincrement();

        @Override
        default KeyGenerator createKeyGenerator() {
            return new KeyGenerator.AutoincrementGenerator(this);
        }
    }
}
