package org.javers.core.diff.changetype.map;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalId;

import java.util.*;
import java.util.function.Predicate;

import static org.javers.common.string.ToStringBuilder.addEnumField;

/**
 * @author bartosz walacik
 */
public class MapChange extends PropertyChange {
    private final List<EntryChange> changes;

    public MapChange(GlobalId affectedCdoId, String propertyName, List<EntryChange> changes) {
        this(affectedCdoId, propertyName, changes, Optional.empty());
    }

    public MapChange(GlobalId affectedCdoId, String propertyName, List<EntryChange> changes, Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, propertyName, commitMetadata);
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
    protected String fieldsToString() {
        StringBuilder changesAsString = new StringBuilder();

        for (EntryChange c : changes){
            if (changesAsString.length() > 0) { changesAsString.append(", "); }
            changesAsString.append(c);
        }
        return super.fieldsToString() + addEnumField("entryChanges", changesAsString);
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

    @Override
    public String toString() {
        return "map changed on '" + getPropertyName() + "' property: " + getEntryChanges();
    }
}
