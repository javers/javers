package org.javers.repository.sql.finders;

class CommitPropertyDTO {
    private long commitPK;
    private String name;
    private String value;

    public CommitPropertyDTO(long commitPK, String name, String value) {
        this.commitPK = commitPK;
        this.name = name;
        this.value = value;
    }

    public long getCommitPK() {
        return commitPK;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
