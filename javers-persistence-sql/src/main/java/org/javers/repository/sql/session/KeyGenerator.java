package org.javers.repository.sql.session;

interface KeyGenerator {

    interface Sequence extends KeyGenerator {
        String nextFromSequenceAsSelect(String seqName);
        String nextFromSequenceEmbedded(String seqName);
    }

    interface Autoincrement extends KeyGenerator {
        String lastInsertedAutoincrement();
    }
}
