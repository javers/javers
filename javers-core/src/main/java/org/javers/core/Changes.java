package org.javers.core;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.jql.JqlQuery;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Convenient wrapper for the list of Changes returned by {@link Javers#findChanges(JqlQuery)}.
 * <br/><br/>
 *
 * Allows traversing over the list of Changes grouped by commits
 * and grouped by entities, see: <br/>
 * {@link #groupByCommit()}, {@link #groupByObject()}.
 * <br/><br/>
 *
 * {@link #prettyPrint()} prints Changes to the the nicely formatted String.
 *
 * @since 3.9
 */
public class Changes extends AbstractList<Change> implements Serializable {
    private final List<Change> changes;
    private final transient PrettyValuePrinter valuePrinter;

    public Changes(List<Change> changes, PrettyValuePrinter valuePrinter) {
        Validate.argumentsAreNotNull(changes, valuePrinter);
        this.changes = Collections.unmodifiableList(changes);
        this.valuePrinter = valuePrinter;
    }

    /**
     * Changes grouped by commits.
     * <br/>
     *
     * When formatting a changelog,
     * usually you need to group changes by commits and then by objects.
     * <br/><br/>
     *
     * For example, this changelog:
     * <pre>
     * commit 2.0
     *   changes on Employee/Frodo :
     *   - ValueChange{ 'salary' changed from '10000' to '11000' }
     *   - ListChange{ 'subordinates' collection changes :
     *   0. 'Employee/Sam' added }
     *   changes on Employee/Sam :
     *   - ValueChange{ 'name' changed from '' to 'Sam' }
     *   - ValueChange{ 'salary' changed from '0' to '2000' }
     *   - ReferenceChange{ 'boss' changed from '' to 'Employee/Frodo' }
     *   - NewObject{ new object: Employee/Sam }
     * commit 1.0
     *   changes on Employee/Frodo :
     *   - ValueChange{ 'name' changed from '' to 'Frodo' }
     *   - ValueChange{ 'salary' changed from '0' to '10000' }
     *   - NewObject{ new object: Employee/Frodo }
     * </pre>
     *
     * is printed by this code:
     * <pre>
     * Changes changes = javers.findChanges(QueryBuilder.byClass(Employee.class)
     *                         .withNewObjectChanges().build());
     *
     * changes.groupByCommit().forEach(byCommit -> {
     *   System.out.println("commit " + byCommit.getCommit().getId());
     *   byCommit.groupByObject().forEach(byObject -> {
     *     System.out.println("  changes on " + byObject.getGlobalId().value() + " : ");
     *     byObject.get().forEach(change -> {
     *       System.out.println("  - " + change);
     *     });
     *   });
     * });
     * </pre>
     *
     * @see <a href="https://javers.org/documentation/repository-examples/#change-log">http://javers.org/documentation/repository-examples/#change-log</a>
     * @since 3.9
     */
    public List<ChangesByCommit> groupByCommit() {
        Map<CommitMetadata, List<Change>> changesByCommit = changes.stream().collect(
                groupingBy(c -> c.getCommitMetadata().orElseThrow( () -> new IllegalStateException("No CommitMetadata in this Change")),
                           () -> new LinkedHashMap<>(), toList()));

        List<ChangesByCommit> result = new ArrayList<>();
        changesByCommit.forEach((k,v) -> {
            result.add(new ChangesByCommit(k, v, valuePrinter));
        });

        return unmodifiableList(result);
    }

    /**
     * Changes grouped by entities.
     * <br/>
     *
     * See example in {@link #groupByCommit()}
     *
     *
     * @since 3.9
     */
    public List<ChangesByObject> groupByObject() {
        Map<GlobalId, List<Change>> changesByObject = changes.stream().collect(
                groupingBy(c -> c.getAffectedGlobalId().masterObjectId()));

        List<ChangesByObject> result = new ArrayList<>();
        changesByObject.forEach((k, v) -> {
            result.add(new ChangesByObject(k, v, valuePrinter));
        });

        return unmodifiableList(result);
    }

    @Override
    public Change get(int index) {
        return changes.get(index);
    }

    @Override
    public int size() {
        return changes.size();
    }

    public <C extends Change> List<C> getChangesByType(final Class<C> type) {
        argumentIsNotNull(type);
        return (List) unmodifiableList(
                changes.stream().filter(input -> type.isAssignableFrom(input.getClass())).collect(Collectors.toList()));
    }

    /**
     * Prints the nicely formatted list of Changes.
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
