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
     * Returns changes grouped by commits.
     * <br/>
     *
     * When formatting a changelog,
     * usually you need to group changes by commits and then by objects.
     * <br/><br/>
     *
     * A simple changelog, like {@link #devPrint()}, can be printed by this code:
     *<br/><br/>
     *
     * <pre>
     * changes.groupByCommit().forEach(byCommit -> {
     *   System.out.println("commit " + byCommit.getCommit().getId());
     *   byCommit.groupByObject().forEach(byObject -> {
     *     System.out.println("  changes on " + byObject.getGlobalId().value() + ":");
     *     byObject.get().forEach(change -> System.out.println("  - " + change));
     *   });
     * });
     * </pre>
     *
     * @see <a href="https://javers.org/documentation/repository-examples/#change-log">http://javers.org/documentation/repository-examples/#change-log</a>
     * @since 3.9
     * TODO javadoc
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
     * Prints the nicely formatted list of Changes. <br/>
     * Can be used on GUI to show Changes to your users.
     * <br/><br/>
     * Example:
     * <br/><br/>
     *
     * <pre>
     * Changes:
     * Commit 2.00 done by author at 14 Mar 2021, 12:37:37 :
     * * changes on Employee/Frodo :
     *   - 'lastPromotionDate' = '14.37.2021 12:37'
     *   - 'performance' map changes :
     *      · entry ['1' : 'bb'] -> ['1' : 'aa']
     *      · entry ['2' : 'bb'] added
     *      · entry ['3' : 'aa'] removed
     *   - 'position' = 'Hero'
     *   - 'postalAddress.city' = 'Shire'
     *   - 'primaryAddress.city' changed: 'Shire' -> 'Mordor'
     *   - 'primaryAddress.street' = 'Some Street'
     *   - 'salary' changed: '10000' -> '12000'
     *   - 'skills' collection changes :
     *      · 'agile coaching' added
     *   - 'subordinates' collection changes :
     *      0. 'Employee/Sam' added
     * * new object: Employee/Sam
     *   - 'name' = 'Sam'
     *   - 'salary' = '10000'
     * Commit 1.00 done by author at 14 Mar 2021, 12:37:37 :
     * * new object: Employee/Frodo
     *   - 'name' = 'Frodo'
     *   - 'performance' map changes :
     *      · entry ['1' : 'bb'] added
     *      · entry ['3' : 'aa'] added
     *   - 'primaryAddress.city' = 'Shire'
     *   - 'salary' = '10000'
     *   - 'skills' collection changes :
     *      · 'management' added
     * </pre>
     */
    public final String prettyPrint() {
        StringBuilder b = new StringBuilder();

        b.append("Changes:\n");
        for (ChangesByCommit c : groupByCommit()){
            b.append(c.prettyPrint());
        }
        return b.toString();
    }

    /**
     * Prints the Changes in a technical style. <br/>
     * Useful for development and debugging. <br/>
     * You can use the implementation of this method as the template to write your own pretty print.
     * <br/><br/>
     *
     * Example:
     * <br/><br/>
     * <pre>
     * Changes (18):
     * commit 2.00
     *   changes on Employee/Frodo :
     *   - ValueChange{ property: 'city', left:'Shire',  right:'Mordor' }
     *   - ValueChange{ property: 'street', left:'',  right:'Some Street' }
     *   - ValueChange{ property: 'position', left:'',  right:'Hero' }
     *   - ValueChange{ property: 'salary', left:'10000',  right:'12000' }
     *   - ListChange{ property: 'subordinates', elementChanges:1 }
     *   - SetChange{ property: 'skills', elementChanges:1 }
     *   - MapChange{ property: 'performance', entryChanges:3 }
     *   - ValueChange{ property: 'lastPromotionDate', left:'',  right:'14 Mar 2021, 12:58:06+0100' }
     *   - InitialValueChange{ property: 'city', left:'',  right:'Shire' }
     *   changes on Employee/Sam :
     *   - NewObject{ new object: Employee/Sam }
     *   - InitialValueChange{ property: 'name', left:'',  right:'Sam' }
     *   - InitialValueChange{ property: 'salary', left:'',  right:'10000' }
     * commit 1.00
     *   changes on Employee/Frodo :
     *   - InitialValueChange{ property: 'city', left:'',  right:'Shire' }
     *   - NewObject{ new object: Employee/Frodo }
     *   - InitialValueChange{ property: 'name', left:'',  right:'Frodo' }
     *   - InitialValueChange{ property: 'salary', left:'',  right:'10000' }
     *   - SetChange{ property: 'skills', elementChanges:1 }
     *   - MapChange{ property: 'performance', entryChanges:2 }
     * <pre>
     */
    public String devPrint() {

        groupByCommit().forEach(byCommit -> {
          System.out.println("commit " + byCommit.getCommit().getId());
          byCommit.groupByObject().forEach(byObject -> {
            System.out.println("  changes on " + byObject.getGlobalId().value() + ":");
            byObject.get().forEach(change -> System.out.println("  - " + change));
          });
        });

        return "";
    }

    /**
     * Delegates to {@link #devPrint()}.<br/>
     * See {@link #prettyPrint()}
     */
    @Override
    public String toString() {
        return devPrint();
    }

}
