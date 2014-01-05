package org.javers.model.domain.changeType;

import org.javers.model.domain.Change;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.mapping.Property;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public abstract class PropertyChange extends Change {
    private final Property property;

    protected PropertyChange(GlobalCdoId globalCdoId, Property property) {
        super(globalCdoId);
        argumentIsNotNull(property);
        this.property = property;
    }

    /**
     * Affected property
     */
    public Property getProperty() {
        return property;
    }
}
