package org.javers.core.commit;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.joda.time.LocalDateTime;

import java.util.Collections;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class Commit {
    private final String id = "";
    private final List<CdoSnapshot> snapshots;
    private final String author;
    private final LocalDateTime commitDate;

    public Commit(String author, List<CdoSnapshot> snapshots) {
        Validate.argumentsAreNotNull(author,snapshots);
        this.author = author;
        this.snapshots = snapshots;
        this.commitDate = new LocalDateTime();
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    /**
     * @return unmodifiableList
     */
    public List<CdoSnapshot> getSnapshots() {
        return Collections.unmodifiableList(snapshots);
    }
}
