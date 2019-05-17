package org.javers.core.diff.changetype;

import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.ValueObjectId;

import java.util.Objects;
import java.util.Optional;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Change on object's property of any type (Value, List, Set, Map, Array or Reference)
 *
 * @author bartosz walacik
 */
public abstract class PropertyChange extends Change {
    private final PropertyChangeType changeType;
    private final String propertyName;

    protected PropertyChange(PropertyChangeMetadata propertyChangeMetadata) {
        super(propertyChangeMetadata.getAffectedCdoId(), Optional.empty(), propertyChangeMetadata.getCommitMetadata());
        this.propertyName = propertyChangeMetadata.getPropertyName();
        this.changeType = propertyChangeMetadata.getChangeType();
    }

    public String getPropertyName(){
        return propertyName;
    }

    public String getPropertyNameWithPath() {
        if (getAffectedGlobalId() instanceof ValueObjectId) {
            return ((ValueObjectId) getAffectedGlobalId()).getFragment() + "." + propertyName;
        }
        return propertyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PropertyChange) {
            PropertyChange that = (PropertyChange) o;
            return super.equals(that) &&
                    Objects.equals(this.propertyName, that.propertyName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), propertyName);
    }

    /**
     * @see PropertyChangeType
     * @since 5.5.0
     */
    public PropertyChangeType getChangeType() {
        return changeType;
    }

    /**
     * @see PropertyChangeType
     * @return <code>changeType == PropertyChangeType.PROPERTY_ADDED</code>
     * @since 5.5.0
     */
    public boolean isPropertyAdded() {
        return changeType == PropertyChangeType.PROPERTY_ADDED;
    }

    /**
     * @see PropertyChangeType
     * @return <code>changeType == PropertyChangeType.PROPERTY_REMOVED</code>
     * @since 5.5.0
     */
    public boolean isPropertyRemoved() {
        return changeType == PropertyChangeType.PROPERTY_REMOVED;
    }

    /**
     * @see PropertyChangeType
     * @return <code>changeType == PropertyChangeType.PROPERTY_VALUE_CHANGED</code>
     * @since 5.5.0
     */
    public boolean isPropertyValueChanged() {
        return changeType == PropertyChangeType.PROPERTY_VALUE_CHANGED;
    }
}
