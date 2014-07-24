package org.javers.core.commit;

/**
 * Consists of two parts : <br>
 * majorId = PREVIOUS.majorId + 1  <br>
 * minorId = shortSequence <br>
 *
 * @see CommitSeqGenerator
 * @author bartosz walacik
 */
public final class CommitId {
    private final long majorId;
    private final int  minorId;

    public CommitId(long majorId, int minorId) {
        this.majorId = majorId;
        this.minorId = minorId;
    }

    @Override
    public String toString() {
        return value();
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
            return this.value().equals(((CommitId)o).value());
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
}
