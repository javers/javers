package org.javers.core.commit;

import org.javers.common.validation.Validate;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.joda.time.LocalDateTime;

import java.util.Collections;
import java.util.List;

/**
 * JaVers commit is similar notion to GIT <i>commit</i> or SVN <i>revision</i>.
 * It records <b>change</b> done by user on application data.
 * <br><br>
 *
 * Commit can affect one or more domain objects ({@link Cdo}).
 * <br><br>
 *
 * Commit holds following data:
 * <ul>
 *     <li>who did change the data - {@link CommitMetadata#getAuthor()} </li>
 *     <li>when the change was made - {@link CommitMetadata#getCommitDate()} </li>
 *     <li>list of atomic changes between two domain object graphs - {@link #getChanges()}</li>
 *     <li>list of Snapshots of <b>affected</b> objects - {@link #getSnapshots()}</li>
 * </ul>
 *
 * @author bartosz walacik
 */
public final class Commit {

    private final CommitMetadata commitMetadata;
    private final List<CdoSnapshot> snapshots;
    private final Diff diff;

    Commit(CommitMetadata commitMetadata, List<CdoSnapshot> snapshots, Diff diff) {
        Validate.argumentsAreNotNull(commitMetadata, snapshots, diff);
        this.commitMetadata = commitMetadata;
        this.snapshots = snapshots;
        this.diff = diff;
    }

    /**
     * Monotonically increasing id,
     * e.g. 1.0, 2.0, ...
     */
    public CommitId getId() {
        return commitMetadata.getId();
    }

    public String getAuthor() {
        return commitMetadata.getAuthor();
    }

    Diff getDiff() {
        return diff;
    }

    public LocalDateTime getCommitDate() {
        return commitMetadata.getCommitDate();
    }

    /**
     * @return unmodifiableList
     */
    public List<CdoSnapshot> getSnapshots() {
        return Collections.unmodifiableList(snapshots);
    }

    /**
     * @return unmodifiableList
     */
    public List<Change> getChanges() {
        return diff.getChanges();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Commit(id:" + commitMetadata.getId());
        b.append(", snapshots:" + snapshots.size());
        b.append(", author:" + commitMetadata.getAuthor());
        b.append(", " + diff.changesSummary());
        b.append(")");
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Commit other = (Commit) o;

        return this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
