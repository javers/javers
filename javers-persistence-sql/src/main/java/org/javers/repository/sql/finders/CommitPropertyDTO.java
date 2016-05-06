package org.javers.repository.sql.finders;

class CommitPropertyDTO {
    private long commitPK;
    private String name;
    private String value;

    CommitPropertyDTO(long commitPK, String name, String value) {
        this.commitPK = commitPK;
        this.name = name;
        this.value = value;
    }

    long getCommitPK() {
        return commitPK;
    }

    String getName() {
        return name;
    }

    String getValue() {
        return value;
    }
}
