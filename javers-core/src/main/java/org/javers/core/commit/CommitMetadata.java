package org.javers.core.commit;

import org.javers.common.validation.Validate;
import org.joda.time.LocalDateTime;

public class CommitMetadata {

    private final String author;
    private final LocalDateTime commitDate;
    private CommitId commitId;

    public CommitMetadata(String author, LocalDateTime commitDate) {
        Validate.argumentsAreNotNull(author, commitDate);

        this.author = author;
        this.commitDate = commitDate;
    }

    public CommitMetadata(String author, LocalDateTime commitDate, CommitId commitId) {
        Validate.argumentsAreNotNull(author, commitDate, commitId);

        this.author = author;
        this.commitDate = commitDate;
        this.commitId = commitId;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    public CommitId getCommitId() {
        return commitId;
    }

    public void setCommitId(CommitId commitId) {
        this.commitId = commitId;
    }
}
