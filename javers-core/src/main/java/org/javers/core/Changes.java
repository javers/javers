package org.javers.core;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class Changes extends AbstractList<Change> {
    private final List<Change> changes;
    private final transient PrettyValuePrinter valuePrinter;

    Changes(List<Change> changes, PrettyValuePrinter valuePrinter) {
        this.changes = changes;
        this.valuePrinter = valuePrinter;
    }

    public List<ChangesInCommit> groupByCommit() {
        Map<CommitMetadata, List<Change>> changesByCommit = changes.stream().collect(
                groupingBy(c -> c.getCommitMetadata().orElseThrow( () -> new IllegalStateException("no CommitMetadata in Change")),
                           () -> new LinkedHashMap<>(), toList()));

        List<ChangesInCommit> result = new ArrayList<>();
        changesByCommit.forEach((k,v) -> {
            result.add(new ChangesInCommit(k, v, valuePrinter));
        });

        return Collections.unmodifiableList(result);
    }

    @Override
    public Change get(int index) {
        return changes.get(index);
    }

    @Override
    public int size() {
        return changes.size();
    }

    /**
     * Prints the nicely formatted list of changes.
     * Alias to {@link #toString()}.
     */
    public final String prettyPrint() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        List<ChangesInCommit> byCommit = groupByCommit();

        b.append("Changes:\n");
        for (ChangesInCommit c : groupByCommit()){
            b.append(c.prettyPrint());
        }
        return b.toString();
    }

    public static final class ChangesInCommit extends AbstractList<Change> {
        private final List<Change> changes;
        private final CommitMetadata commitMetadata;
        private final transient PrettyValuePrinter valuePrinter;

        ChangesInCommit(CommitMetadata commitMetadata, List<Change> changes, PrettyValuePrinter valuePrinter) {
            Validate.argumentsAreNotNull(commitMetadata, changes, valuePrinter);
            this.changes = changes;
            this.commitMetadata = commitMetadata;
            this.valuePrinter = valuePrinter;
        }

        /**
         * Prints the nicely formatted list of changes in a given commit.
         * Alias to {@link #toString()}.
         */
        public final String prettyPrint() {
            return toString();
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();

            b.append("Commit " + commitMetadata.getId() +
                    " done by " + commitMetadata.getAuthor() +
                    " at " + valuePrinter.format(commitMetadata.getCommitDate()) +
                    ", changes :\n");

            int i=1;
            for (Change change : changes){
                b.append("  " + (i++) + ". " + change.prettyPrint(valuePrinter) + "\n");
            }
            return b.toString();
        }

        @Override
        public Change get(int index) {
            return changes.get(index);
        }

        @Override
        public int size() {
            return changes.size();
        }

        public CommitMetadata getCommit() {
            return commitMetadata;
        }
    }
}
