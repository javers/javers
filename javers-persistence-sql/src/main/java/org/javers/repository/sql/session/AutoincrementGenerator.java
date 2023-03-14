package org.javers.repository.sql.session;

import org.javers.repository.sql.KeyGenerator;

class AutoincrementGenerator implements KeyGenerator {
    private final KeyGeneratorDefinition.AutoincrementDefinition autoincrementDefinition;

    AutoincrementGenerator(KeyGeneratorDefinition.AutoincrementDefinition autoincrementDefinition) {
        this.autoincrementDefinition = autoincrementDefinition;
    }

    @Override
    public long generateKey(String sequenceName, Session session) {
        throw new RuntimeException("Not implemented. Can't generate key on AutoIncremented");
    }

    @Override
    public long getKeyFromLastInsert(Session session) {
        return session.executeQueryForLong(new Select("last autoincrementDefinition id", autoincrementDefinition.lastInsertedAutoincrement()));
    }

    public void reset() {
    }
}