package org.javers.core.diff.changetype.container;

import org.javers.common.validation.Validate;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.common.string.ToStringBuilder.addEnumField;

/**
 * Collection or Array change
 *
 * @author bartosz walacik
 */
public abstract class ContainerChange extends PropertyChange {
    private final List<ContainerElementChange> changes;

    ContainerChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes) {
        super(affectedCdoId, propertyName);
        Validate.argumentIsNotNull(changes);
        Validate.argumentCheck(!changes.isEmpty(),"changes list should not be empty");
        this.changes = Collections.unmodifiableList(new ArrayList<>(changes));
    }

    /**
     * @return unmodifiable list
     */
    public List<ContainerElementChange> getChanges() {
        return changes;
    }

    @Override
    protected String fieldsToString() {
        StringBuilder changesAsString = new StringBuilder();

        for (ContainerElementChange c : changes){
            if (changesAsString.length() > 0) { changesAsString.append(", "); }
            changesAsString.append(c);
        }
        return super.fieldsToString() + addEnumField("containerChanges", changesAsString);
    }
}
