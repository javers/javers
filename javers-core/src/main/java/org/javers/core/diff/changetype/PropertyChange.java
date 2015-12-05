package org.javers.core.diff.changetype;

import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;
import static org.javers.common.string.ToStringBuilder.addField;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Property change like {@link ValueChange} or {@link ReferenceChange}
 *
 * @author bartosz walacik
 */
public abstract class PropertyChange extends Change {
    private final String propertyName;

    protected PropertyChange(GlobalId affectedCdoId, String propertyName) {
        super(affectedCdoId);
        argumentIsNotNull(propertyName);
        this.propertyName = propertyName;
    }

    public String getPropertyName(){
        return propertyName;
    }

    @Override
    protected String fieldsToString() {
        return super.fieldsToString() + addField("property", propertyName);
    }
}
