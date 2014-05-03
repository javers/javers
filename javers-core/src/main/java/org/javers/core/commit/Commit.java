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
    private final CommitId id;
    private final List<CdoSnapshot> snapshots;
    private final String author;
    private final LocalDateTime commitDate;
    private final Diff diff;

    Commit(CommitId id, String author, List<CdoSnapshot> snapshots, Diff diff) {
        Validate.argumentsAreNotNull(id, author, snapshots, diff);
        this.author = author;
        this.snapshots = snapshots;
        this.commitDate = new LocalDateTime();
        this.diff = diff;
        this.id = id;
        for (CdoSnapshot snapshot : snapshots){
            snapshot.bindTo(id);
        }
    }

    /**
     * Monotonically increasing id
     */
    public CommitId getId() {
        return id;
    }

    public GlobalCdoId getGlobalCdoId() {
        return snapshots.get(0).getGlobalId();
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

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Commit(id:" + id);
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

        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
