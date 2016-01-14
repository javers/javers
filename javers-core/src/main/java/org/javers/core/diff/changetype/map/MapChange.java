package org.javers.core.diff.changetype.map;

import org.javers.common.validation.Validate;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.common.string.ToStringBuilder.addEnumField;

/**
 * @author bartosz walacik
 */
public class MapChange extends PropertyChange {
    private final List<EntryChange> changes;

    public MapChange(GlobalId affectedCdoId, String propertyName, List<EntryChange> changes) {
        super(affectedCdoId, propertyName);
        Validate.argumentIsNotNull(changes);
        Validate.argumentCheck(!changes.isEmpty(),"changes list should not be empty");
        this.changes = Collections.unmodifiableList(new ArrayList<>(changes));
    }

    /**
     * @return unmodifiable list
     */
    public List<EntryChange> getEntryChanges() {
        return changes;
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
}
