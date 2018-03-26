package org.javers.core.diff;

import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.string.PrettyValuePrinter;
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
 * Diff is a set of (atomic) changes between two graphs of objects.
 * <br><br>
 *
 * Typically it is used to capture and trace changes made by user on his domain data.
 * In this case diff is done between previous and current state of a bunch of domain objects.
 * <br><br>
 *
 * @author bartosz walacik
 */
public class Diff implements Serializable {

    private final List<Change> changes;
    private final transient PrettyValuePrinter valuePrinter;

    Diff(List<Change> changes, PrettyValuePrinter valuePrinter) {
        this.changes = changes;
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
     * Full list of changes
     *
     * @return unmodifiable list
     */
    public List<Change> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    /**
     * Changes that satisfies given filter condition
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
     * Prints the nicely formatted list of changes.
     * Alias to {@link #toString()}.
     */
    public final String prettyPrint() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        b.append("Diff:\n");

        int i=1;
        for (Change change : changes){
            b.append((i++) + ". " + change.prettyPrint(valuePrinter) + "\n");
        }
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
