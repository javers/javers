package org.javers.core.commit;

import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.joda.time.LocalDateTime;

public class CommitMetadata {

    private final String author;
    private final LocalDateTime commitDate;
    private CommitId id;

    public CommitMetadata(String author, LocalDateTime commitDate, CommitId id) {
        Validate.argumentsAreNotNull(author, commitDate, id);

        this.author = author;
        this.commitDate = commitDate;
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    public CommitId getId() {
        return id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, "author", author, "date", commitDate, "id", id);
    }
}
