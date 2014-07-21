package org.javers.core.commit;

import org.javers.common.validation.Validate;
import org.javers.core.diff.Diff;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.joda.time.LocalDateTime;

import java.util.Collections;
import java.util.List;

/**
 * JaVers commit is similar notion to <i>commit</i> in GIT or <i>revision</i> in SVN.
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
     * Monotonically increasing id
     */
    public CommitId getId() {
        return commitMetadata.getCommitId();
    }

    public GlobalCdoId getGlobalCdoId() {
        return snapshots.get(0).getGlobalId();
    }

    public String getAuthor() {
        return commitMetadata.getAuthor();
    }

    public Diff getDiff() {
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

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Commit(id:" + commitMetadata.getCommitId());
        b.append(", snapshots:" + snapshots.size());
        b.append(", " + diff.toString());
        b.append(")");
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Commit other = (Commit) o;

        return this.commitMetadata.getCommitId().equals(other.commitMetadata.getCommitId());
    }

    @Override
    public int hashCode() {
        return commitMetadata.getCommitId().hashCode();
    }
}
