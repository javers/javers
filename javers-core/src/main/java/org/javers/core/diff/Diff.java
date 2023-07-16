package org.javers.core.diff;

import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.core.Changes;
import org.javers.core.ChangesByObject;
import org.javers.core.diff.changetype.PropertyChange;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.javers.common.exception.JaversExceptionCode.AFFECTED_CDO_IS_NOT_AVAILABLE;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Diff is a list of changes between two object graphs.
 * <br><br>
 *
 * Typically, it is used to capture and trace changes made on domain objects.
 * In this case, diff is done between previous and current state of an object graph.
 * <br><br>
 *
 * <ul>
 * <li/>{@link #getChanges()} returns a flat list of Changes
 *
 * <li/>{@link #groupByObject()} returns Changes grouped by objects
 *
 * <li/>{@link #prettyPrint()} prints Changes to the the nicely formatted String
 * </ul>
 *
 * @author bartosz walacik
 */
public class Diff implements Serializable {

    private final Changes changes;
    private final transient PrettyValuePrinter valuePrinter;

    Diff(List<Change> changes, PrettyValuePrinter valuePrinter) {
        this.changes = new Changes(changes, valuePrinter);
        this.valuePrinter = valuePrinter;
    }

    /**
     * Selects new, removed or changed objects
     *
     * @throws JaversException AFFECTED_CDO_IS_NOT_AVAILABLE if diff is restored from a repository
     */
    public <C extends Change> List getObjectsByChangeType(final Class<C> type) {
        argumentIsNotNull(type);

        return Lists.transform(getChangesByType(type),
                input -> input.getAffectedObject().<JaversException>orElseThrow(() -> new JaversException(AFFECTED_CDO_IS_NOT_AVAILABLE)));
    }

    /**
     * Selects objects
     * with changed property for given property name
     *
     * @throws JaversException AFFECTED_CDO_IS_NOT_AVAILABLE if diff is restored from repository,
     */
    public List getObjectsWithChangedProperty(String propertyName){
        argumentIsNotNull(propertyName);

        return Lists.transform(getPropertyChanges(propertyName),
                input -> input.getAffectedObject().<JaversException>orElseThrow(() -> new JaversException(AFFECTED_CDO_IS_NOT_AVAILABLE)));
    }

    /**
     * Flat list of changes
     *
     */
    public Changes getChanges() {
        return changes;
    }

    /**
     * Changes grouped by entities
     *
     * @since 3.9
     */
    public List<ChangesByObject> groupByObject() {
       return new Changes(changes, valuePrinter).groupByObject();
    }

    /**
     * Changes that satisfies given filter
     */
    public List<Change> getChanges(Predicate<Change> predicate) {
        return Lists.positiveFilter(changes, predicate);
    }

    public <C extends Change> List<C> getChangesByType(final Class<C> type) {
        argumentIsNotNull(type);
        return (List)getChanges(input -> type.isAssignableFrom(input.getClass()));
    }

    /**
     * Selects property changes for given property name
     */
    public List<PropertyChange> getPropertyChanges(final String propertyName) {
        argumentIsNotNull(propertyName);
        return (List)getChanges(input -> input instanceof PropertyChange && ((PropertyChange)input).getPropertyName().equals(propertyName));
    }

    public boolean hasChanges() {
        return !changes.isEmpty();
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

        b.append("Diff:\n");

        groupByObject().forEach(it -> b.append(it.toString()));

        return b.toString();
    }

    public String changesSummary(){
        StringBuilder b = new StringBuilder();

        b.append("changes - ");
        for (Map.Entry<Class<? extends Change>, Integer> e : countByType().entrySet()){
            b.append(e.getKey().getSimpleName()+ ":"+e.getValue()+" ");
        }
        return b.toString().trim();
    }

    public Map<Class<? extends Change>, Integer> countByType(){
        Map<Class<? extends Change>, Integer> result = new HashMap<>();
        for(Change change : changes) {
            Class<? extends Change> key = change.getClass();
            if (result.containsKey(change.getClass())){
                result.put(key, (result.get(key))+1);
            }else{
                result.put(key, 1);
            }
        }
        return result;
    }
}
