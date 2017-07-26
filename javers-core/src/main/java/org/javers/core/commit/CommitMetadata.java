package org.javers.core.commit;

import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import java.time.LocalDateTime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.unmodifiableMap;

public class CommitMetadata implements Serializable {

    private final String author;
    private final Map<String, String> properties;
    private final LocalDateTime commitDate;
    private final CommitId id;

    public CommitMetadata(String author, Map<String, String> properties, LocalDateTime commitDate, CommitId id) {
        Validate.argumentsAreNotNull(author, properties, commitDate, id);

        this.author = author;
        this.properties = new HashMap<>(properties);
        this.commitDate = commitDate;
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public Map<String, String> getProperties() {
        return unmodifiableMap(properties);
    }

    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    public CommitId getId() {
        return id;
    }

    public boolean isBeforeOrEqual(CommitMetadata that){
        return this.id.isBeforeOrEqual(that.id);
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, "author", author, "properties", properties, "util", commitDate, "id", id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommitMetadata that = (CommitMetadata) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
