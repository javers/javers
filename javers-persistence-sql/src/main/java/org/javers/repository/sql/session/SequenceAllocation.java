package org.javers.repository.sql.session;

import org.javers.repository.sql.KeyGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SequenceAllocation implements KeyGenerator {

    private final Object lock = new Object();

    private final KeyGeneratorDefinition.SequenceDefinition sequenceDefinition;

    private Map<String, Sequence> sequences = new ConcurrentHashMap();

    private ThreadLocal<Long> lastKey = new ThreadLocal<>();

    SequenceAllocation(KeyGeneratorDefinition.SequenceDefinition sequenceDefinition) {
        this.sequenceDefinition = sequenceDefinition;
    }

    String nextFromSequenceAsSQLExpression(String seqName) {
        return sequenceDefinition.nextFromSequenceAsSQLExpression(seqName);
    }

    @Override
    public long generateKey(String sequenceName, Session session) {
        long nextVal = findSequence(sequenceName).nextValue(session);
        lastKey.set(nextVal);
        return nextVal;
    }

    private Sequence findSequence(String sequenceName) {
        if (!sequences.containsKey(sequenceName)) {
            synchronized (lock) {
                //double check, condition could change while obtaining the lock
                if (!sequences.containsKey(sequenceName)) {
                    Sequence sequence = new Sequence(sequenceName, sequenceDefinition);
                    sequences.put(sequenceName, sequence);
                }
            }
        }

        return sequences.get(sequenceName);
    }

    @Override
    public long getKeyFromLastInsert(Session session) {
        return lastKey.get();
    }

    @Override
    public void reset() {
        synchronized (lock) {
            sequences.clear();
        }
    }
}
