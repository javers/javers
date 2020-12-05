package org.javers.repository.sql.session;

import org.javers.repository.sql.session.KeyGeneratorDefinition.SequenceDefinition;
import org.polyjdbc.core.exception.SequenceLimitReachedException;

/**
 * forked from org.polyjdbc.core.key.Sequence
 *
 * @author Adam Dubiel
 */
final class Sequence {
    private static final long SEQUENCE_ALLOCATION_SIZE = 100;

    private final String sequenceName;
    private final SequenceDefinition sequenceGenerator;

    private long currentValue;
    private long currentLimit = -1;

    Sequence(String sequenceName, SequenceDefinition sequenceGenerator) {
        this.sequenceName = sequenceName;
        this.sequenceGenerator = sequenceGenerator;
    }

    synchronized long nextValue(Session session) {
        if (recalculationNeeded()) {
            long currentSequenceValue = session.executeQueryForLong(
                    new Select("SELECT next from seq "+ sequenceName,
                            sequenceGenerator.nextFromSequenceAsSelect(sequenceName)));
            recalculate(currentSequenceValue);
        }
        return nextLocalValue();
    }

    long nextLocalValue() {
        if(recalculationNeeded()) {
            throw new SequenceLimitReachedException("Sequence " + sequenceName + " has reached its limit of " + currentLimit + ". "
                    + "Before fetching value, check if recalculation is needed using recalculationNeeded() method.");
        }
        currentValue++;
        return currentValue - 1;
    }

    void recalculate(long currentSequenceValue) {
        currentValue = SEQUENCE_ALLOCATION_SIZE * currentSequenceValue ;
        currentLimit = SEQUENCE_ALLOCATION_SIZE * (currentSequenceValue + 1) - 1;
    }

    boolean recalculationNeeded() {
        return currentValue > currentLimit;
    }
}
