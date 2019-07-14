package org.javers.core.diff.changetype.map;

import org.javers.common.collections.Lists;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.PropertyChangeMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Changes on a Map property
 *
 * @author bartosz walacik
 */
public class MapChange extends PropertyChange {
    private final List<EntryChange> changes;

    public MapChange(PropertyChangeMetadata metadata, List<EntryChange> changes) {
        super(metadata);
        Validate.argumentIsNotNull(changes);
        Validate.argumentCheck(!changes.isEmpty(),"changes list should not be empty");
        this.changes = Collections.unmodifiableList(new ArrayList<>(changes));
    }

    public List<EntryChange> getEntryChanges() {
        return changes;
    }

    public List<EntryAdded> getEntryAddedChanges() {
        return filterChanges(EntryAdded.class);
    }

    public List<EntryRemoved> getEntryRemovedChanges() {
        return filterChanges(EntryRemoved.class);
    }

    public List<EntryValueChange> getEntryValueChanges() {
        return filterChanges(EntryValueChange.class);
    }

    private <T extends EntryChange> List<T> filterChanges(final Class<T> ofType) {
        return (List) Lists.positiveFilter(changes, input -> ofType.isAssignableFrom(input.getClass()));
    }

    @Override
    public String prettyPrint(PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);
        StringBuilder builder = new StringBuilder();

        builder.append(valuePrinter.formatWithQuotes(getPropertyNameWithPath()) + " map changes :\n");

        changes.forEach(cc -> builder.append("  " + cc.prettyPrint(valuePrinter)+"\n"));

        String result = builder.toString();
        return result.substring(0, result.length() - 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MapChange) {
            MapChange that = (MapChange) obj;
            return super.equals(that)
                    && Objects.equals(this.changes, that.changes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.changes);
    }
}
