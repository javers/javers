package org.javers.repository.jql;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;

/**
 * Queries for Snapshots or Changes by {@link GlobalId} and changed property
 *
 * @author bartosz.walacik
 */
class PropertyFilter extends Filter {
    private final String propertyName;

    PropertyFilter(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String toString() {
        return "property=" + propertyName;
    }
}
