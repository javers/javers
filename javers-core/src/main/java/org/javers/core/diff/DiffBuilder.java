package org.javers.core.diff;

import org.javers.core.commit.CommitMetadata;

import java.util.*;

/**
 * @author bartosz walacik
 */
class DiffBuilder {
    private final List<Change> changes = new ArrayList<>();

    private DiffBuilder() {
    }

    public static DiffBuilder diff() {
        return new DiffBuilder();
    }

    public static Diff empty() {
        return new Diff(Collections.<Change>emptyList());
    }

    public DiffBuilder addChange(Change change, Optional<Object> affectedCdo) {
        addChange(change);
        affectedCdo.ifPresent(change::setAffectedCdo);
        return this;
    }

    public DiffBuilder addChange(Change change) {
        changes.add(change);
        return this;
    }

    public DiffBuilder addChanges(Collection<Change> changeSet, final Optional<CommitMetadata> commitMetadata) {

        changeSet.forEach(change -> {
            addChange(change);
            commitMetadata.ifPresent(it -> change.bindToCommit(it));
        });

        return this;
    }

    public Diff build() {
        return new Diff(changes);
    }
}
