package org.javers.core.commit;

import org.javers.common.validation.Validate;
import org.javers.core.diff.Diff;
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
    private final Diff diff;

    public Commit(String author, List<CdoSnapshot> snapshots, Diff diff) {
        Validate.argumentsAreNotNull(author,snapshots, diff);
        this.author = author;
        this.snapshots = snapshots;
        this.commitDate = new LocalDateTime();
        this.diff = diff;
    }

    public String getAuthor() {
        return author;
    }

    public Diff getDiff() {
        return diff;
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

    public String shortDesc() {
        StringBuilder b = new StringBuilder();
        b.append("Commit(");
        b.append("snapshots:" + snapshots.size());
        b.append(", " + diff.shortDesc());
        b.append(")");
        return b.toString();
    }
}
