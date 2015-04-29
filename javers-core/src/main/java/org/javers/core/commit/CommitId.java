package org.javers.core.commit;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;

import java.math.BigDecimal;

/**
 * Consists of two parts : <br>
 * majorId = PREVIOUS.majorId + 1  <br>
 * minorId = shortSequence <br>
 *
 * @see CommitSeqGenerator
 * @author bartosz walacik
 */
public final class CommitId implements Comparable<CommitId> {
    private final long majorId;
    private final int  minorId;

    public CommitId(long majorId, int minorId) {
        this.majorId = majorId;
        this.minorId = minorId;
    }

    public static CommitId valueOf(BigDecimal majorDotMinor) {
        Validate.argumentIsNotNull(majorDotMinor);

        long major = majorDotMinor.longValue();
        double minor = (majorDotMinor.doubleValue() - major) * 100;

        return new CommitId(major, (int)minor);
    }

    public static CommitId valueOf(String majorDotMinor) {
        Validate.argumentIsNotNull(majorDotMinor);

        String[] strings = majorDotMinor.split("\\.");

        if (strings.length != 2) {
            throw new JaversException(JaversExceptionCode.CANT_PARSE_COMMIT_ID, majorDotMinor);
        }

        long major = Long.parseLong(strings[0]);
        int minor = Integer.parseInt(strings[1]);

        return new CommitId(major, minor);
    } 
    
    @Override
    public String toString() {
        return value();
    }

    public BigDecimal valueAsNumber(){
        return new BigDecimal(majorId+(minorId*.01)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public String value(){
        return majorId+"."+minorId;
    }

    public long getMajorId() {
        return majorId;
    }

    public int getMinorId() {
        return minorId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o instanceof CommitId) {
            return this.valueAsNumber().equals(((CommitId)o).valueAsNumber());
        }
        if (o instanceof String) {
            return this.value().equals(o);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return value().hashCode();
    }

    @Override
    public int compareTo(CommitId o) {
        return this.valueAsNumber().compareTo(o.valueAsNumber());
    }
}
