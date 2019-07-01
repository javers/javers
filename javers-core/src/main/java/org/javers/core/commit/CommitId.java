package org.javers.core.commit;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @see CommitSeqGenerator
 * @author bartosz walacik
 */
public final class CommitId implements Comparable<CommitId>, Serializable {

    private final long majorId;
    private final int  minorId;

    public CommitId(long majorId, int minorId) {
        this.majorId = majorId;
        this.minorId = minorId;
    }

    public static CommitId valueOf(BigDecimal majorDotMinor) {
        Validate.argumentIsNotNull(majorDotMinor);

        long major = majorDotMinor.longValue();
        BigDecimal minorFractional = majorDotMinor.subtract(BigDecimal.valueOf(major));
        BigDecimal minor = minorFractional.movePointRight(2);

        return new CommitId(major, minor.setScale(0, RoundingMode.HALF_UP).intValue());
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
        BigDecimal major = BigDecimal.valueOf(majorId);
        BigDecimal minorFractional = BigDecimal.valueOf(minorId).movePointLeft(2);
        return major.add(minorFractional).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isBeforeOrEqual(CommitId that){
        return valueAsNumber().compareTo(that.valueAsNumber()) <= 0;
    }

    /**
     * e.g. "1.0"
     */
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
        return valueAsNumber().hashCode();
    }

    @Override
    public int compareTo(CommitId o) {
        return this.valueAsNumber().compareTo(o.valueAsNumber());
    }
}
