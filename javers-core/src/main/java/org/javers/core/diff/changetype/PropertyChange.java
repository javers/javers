package org.javers.core.diff.changetype;

import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import static org.javers.common.string.ToStringBuilder.addField;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Property change like {@link ValueChange} or {@link ReferenceChange}
 *
 * @author bartosz walacik
 */
public abstract class PropertyChange extends Change {
    private final Property property;

    protected PropertyChange(GlobalId affectedCdoId, Property property) {
        super(affectedCdoId);
        argumentIsNotNull(property);
        this.property = property;
    }

    /**
     * Affected property
     */
    public Property getProperty() {
        return property;
    }

    public String getPropertyName(){
        return property.getName();
    }

    @Override
    protected String fieldsToString() {
        return super.fieldsToString() + addField("property", property.getName());
    }
}
