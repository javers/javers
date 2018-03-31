package org.javers.core;

import com.google.common.base.Preconditions;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.object.ValueObjectId;

import java.util.*;

import static java.util.stream.Collectors.*;

public class Changes extends AbstractList<Change> {
    private final List<Change> changes;
    private final transient PrettyValuePrinter valuePrinter;

    Changes(List<Change> changes, PrettyValuePrinter valuePrinter) {
        this.changes = changes;
        this.valuePrinter = valuePrinter;
    }

    public List<ChangesByCommit> groupByCommit() {
        Map<CommitMetadata, List<Change>> changesByCommit = changes.stream().collect(
                groupingBy(c -> c.getCommitMetadata().orElseThrow( () -> new IllegalStateException("no CommitMetadata in Change")),
                           () -> new LinkedHashMap<>(), toList()));

        List<ChangesByCommit> result = new ArrayList<>();
        changesByCommit.forEach((k,v) -> {
            result.add(new ChangesByCommit(k, v, valuePrinter));
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

        List<ChangesByCommit> byCommit = groupByCommit();

        b.append("Changes:\n");
        for (ChangesByCommit c : groupByCommit()){
            b.append(c.prettyPrint());
        }
        return b.toString();
    }

    public static final class ChangesByCommit extends AbstractList<Change> {
        private final List<Change> changes;
        private final CommitMetadata commitMetadata;
        private final transient PrettyValuePrinter valuePrinter;

        ChangesByCommit(CommitMetadata commitMetadata, List<Change> changes, PrettyValuePrinter valuePrinter) {
            Validate.argumentsAreNotNull(commitMetadata, changes, valuePrinter);
            this.changes = changes;
            this.commitMetadata = commitMetadata;
            this.valuePrinter = valuePrinter;
        }

        public List<ChangesByObject> groupByObject() {
            Map<GlobalId, List<Change>> changesByObject = changes.stream().collect(
                    groupingBy(c -> getMasterObjectId(c.getAffectedGlobalId())));

            List<ChangesByObject> result = new ArrayList<>();
            changesByObject.forEach((k,v) -> {
                result.add(new ChangesByObject(k, v, commitMetadata, valuePrinter));
            });

            return Collections.unmodifiableList(result);
        }

        private static GlobalId getMasterObjectId(GlobalId globalId) {
            Preconditions.checkArgument(globalId != null);
            if (globalId instanceof InstanceId || globalId instanceof UnboundedValueObjectId) {
                return globalId;
            }

            if (globalId instanceof ValueObjectId) {
                return ((ValueObjectId)globalId).getOwnerId();
            }

            throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
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
                    " :\n");

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

    public static final class ChangesByObject extends AbstractList<Change> {
        private final List<Change> changes;
        private final CommitMetadata commitMetadata;
        private final GlobalId globalId;
        private final transient PrettyValuePrinter valuePrinter;

        public ChangesByObject(GlobalId globalId, List<Change> changes, CommitMetadata commitMetadata, PrettyValuePrinter valuePrinter) {
            this.changes = changes;
            this.commitMetadata = commitMetadata;
            this.globalId = globalId;
            this.valuePrinter = valuePrinter;
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
         * Id of changed Entity.
         * For Value Objects -- id of owning Entity.
         */
        public GlobalId getGlobalId() {
            return globalId;
        }

        public CommitMetadata getCommit() {
            return commitMetadata;
        }
    }
}
