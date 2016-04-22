package org.javers.repository.sql.finders;

class CommitPropertyDTO {
    private long commitPK;
    private String key;
    private String value;

    public CommitPropertyDTO(long commitPK, String key, String value) {
        this.commitPK = commitPK;
        this.key = key;
        this.value = value;
    }

    public long getCommitPK() {
        return commitPK;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
