package org.javers.core;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalId;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * List of Changes done on a specific object.
 *
 * <ul>
 * <li/>{@link #getGlobalId()}} changed object Id
 * <li/>{@link #get()} list of Changes
 * </ul>
 */
public final class ChangesByObject {
    private final List<Change> changes;
    private final GlobalId globalId;
    private final transient PrettyValuePrinter valuePrinter;

    ChangesByObject(GlobalId globalId, List<Change> changes, PrettyValuePrinter valuePrinter) {
        this.changes = changes;
        this.globalId = globalId;
        this.valuePrinter = valuePrinter;
    }

    public List<Change> get() {
        return Collections.unmodifiableList(changes);
    }

    /**
     * Id of a changed Entity.
     * <br/>
     * For Value Objects &mdash; Id of an owning Entity.
     */
    public GlobalId getGlobalId() {
        return globalId;
    }

    public List<NewObject> getNewObjects() {
        return (List) changes.stream().filter(c -> c instanceof NewObject)
                .sorted(Comparator.comparingInt(it -> it.getAffectedGlobalId().value().length()))
                .collect(Collectors.toList());
    }

    public List<ObjectRemoved> getObjectsRemoved() {
        return (List) changes.stream().filter(c -> c instanceof ObjectRemoved)
                .sorted(Comparator.comparingInt(it -> it.getAffectedGlobalId().value().length()))
                .collect(Collectors.toList());
    }

    public List<PropertyChange> getPropertyChanges() {
        return (List) changes.stream().filter(c -> c instanceof PropertyChange)
                .sorted(Comparator.comparing(a -> ((PropertyChange) a).getPropertyNameWithPath()))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        getNewObjects().forEach(c ->
                b.append("* " + c.prettyPrint(valuePrinter) + "\n")
        );

        getObjectsRemoved().forEach(c ->
                b.append("* " + c.prettyPrint(valuePrinter) + "\n")
        );

        if (!getPropertyChanges().isEmpty()) {
            b.append("* changes on " + globalId.value() + " :\n");
        }
        getPropertyChanges().forEach(c ->
                b.append("  - " + c.prettyPrint(valuePrinter).replace("\n", "\n  ") + "\n")
        );

        return b.toString();
    }
}
