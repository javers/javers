package org.javers.core.diff;

import org.javers.common.collections.Consumer;
import org.javers.common.collections.Optional;
import org.javers.core.commit.CommitMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class DiffBuilder {
    private final List<Change> changes = new ArrayList<>();

    private DiffBuilder() {
    }

    public static DiffBuilder diff(){
        return new DiffBuilder();
    }

    public static Diff empty(){
        return new Diff(Collections.EMPTY_LIST);
    }

    public DiffBuilder addChange(Change change, Optional<Object> affectedCdo) {
        addChange(change);
        change.setAffectedCdo(affectedCdo);
        return this;
    }

    public DiffBuilder addChange(Change change) {
        changes.add(change);
        return this;
    }

    public DiffBuilder addChanges(Collection<Change> changeSet, final Optional<CommitMetadata> commitMetadata) {
        if (commitMetadata.isPresent()) {
            addChanges(changeSet, new Consumer<Change>() {
                @Override
                public void consume(Change change) {
                    change.bindToCommit(commitMetadata.get());
                }
            });
        } else {
            addChanges(changeSet, new Consumer<Change>() {
                @Override
                public void consume(Change change) {

                }
            });
        }

        return this;
    }

    private void addChanges(Collection<Change> changeSet, Consumer<Change> consumer) {
        for (Change change : changeSet) {
            addChange(change);
            consumer.consume(change);
        }
    }

    public Diff build() {
        return new Diff(changes);
    }
}
