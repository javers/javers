package org.javers.core.commit;

import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.unmodifiableMap;

public class CommitMetadata implements Serializable {

    private final String author;
    private final Map<String, String> properties;
    private final LocalDateTime commitDate;
    private final Instant commitDateInstant;
    private final CommitId id;

    public CommitMetadata(String author, Map<String, String> properties, LocalDateTime commitDate,
                          Instant commitDateInstant,
                          CommitId id) {
        Validate.argumentsAreNotNull(author, properties, commitDate, id);

        this.author = author;
        this.properties = new HashMap<>(properties);
        this.commitDate = commitDate;
        this.id = id;
        this.commitDateInstant = initCommitDateInstant(commitDate, commitDateInstant);
    }

    public static CommitMetadata nullObject() {
        return NullCommitMetadata.instance();
    }

    private Instant initCommitDateInstant(LocalDateTime commitDate, Instant commitDateInstant) {
        if (commitDateInstant != null) {
            return commitDateInstant;
        }

        //for old records without commitDateInstant
        return commitDate.toInstant(ZonedDateTime.now().getOffset());
    }

    public String getAuthor() {
        return author;
    }

    public Map<String, String> getProperties() {
        return unmodifiableMap(properties);
    }

    /**
     * Created date as local date.
     * <br/><br/>
     *
     * CommitDate is persisted as LocalDateTime
     * (without information about time zone and daylight saving time).
     * <br/><br/>
     *
     * It may affects your query results. For example,
     * once a year when DST ends,
     * one hour is repeated (clock goes back from 3 am to 2 am).
     * Looking just on the commitDate we
     * can't distinct in which <i>iteration</i> of the hour, given commit was made.
     */
    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    /**
     * Created date as UTC timestamp
     */
    public Instant getCommitDateInstant() {
        return commitDateInstant;
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
