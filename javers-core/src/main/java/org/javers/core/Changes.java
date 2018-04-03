package org.javers.core;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.jql.JqlQuery;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Convenient wrapper for the list of Changes returned by {@link Javers#findChanges(JqlQuery)}.
 * <br/><br/>
 *
 * Allows iterating over the list of Changes grouped by commits
 * and grouped by entities.
 *
 * TODO example
 *
 * @since 3.9
 */
public class Changes extends AbstractList<Change> {
    private final List<Change> changes;
    private final transient PrettyValuePrinter valuePrinter;

    public Changes(List<Change> changes, PrettyValuePrinter valuePrinter) {
        Validate.argumentsAreNotNull(changes, valuePrinter);
        this.changes = changes;
        this.valuePrinter = valuePrinter;
    }

    /**
     * Changes grouped by commits
     */
    public List<ChangesByCommit> groupByCommit() {
        Map<CommitMetadata, List<Change>> changesByCommit = changes.stream().collect(
                groupingBy(c -> c.getCommitMetadata().orElseThrow( () -> new IllegalStateException("No CommitMetadata in this Change")),
                           () -> new LinkedHashMap<>(), toList()));

        List<ChangesByCommit> result = new ArrayList<>();
        changesByCommit.forEach((k,v) -> {
            result.add(new ChangesByCommit(k, v, valuePrinter));
        });

        return Collections.unmodifiableList(result);
    }

    /**
     * Changes grouped by entities
     */
    public List<ChangesByObject> groupByObject() {
        Map<GlobalId, List<Change>> changesByObject = changes.stream().collect(
                groupingBy(c -> c.getAffectedGlobalId().getMasterObjectId()));

        List<ChangesByObject> result = new ArrayList<>();
        changesByObject.forEach((k, v) -> {
            result.add(new ChangesByObject(k, v, valuePrinter));
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

        b.append("Changes:\n");
        for (ChangesByCommit c : groupByCommit()){
            b.append(c.prettyPrint());
        }
        return b.toString();
    }

}
