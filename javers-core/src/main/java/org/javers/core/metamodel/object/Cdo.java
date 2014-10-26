package org.javers.core.metamodel.object;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.Entity;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.clazz.ValueObject;

/**
 * Abstract holder for client's domain object, {@link Entity} or {@link ValueObject}
 *
 * @author bartosz walacik
 */
public abstract class Cdo {
    private final GlobalId globalId;

    protected Cdo(GlobalId globalId) {
        Validate.argumentIsNotNull(globalId);
        this.globalId = globalId;
    }

    public GlobalId getGlobalId() {
        return globalId;
    }

    /**
     * shortcut to {@link GlobalId#getCdoClass()}
     */
    public ManagedClass getManagedClass() {
        return globalId.getCdoClass();
    }

    public abstract Optional<Object> getWrappedCdo();

    public abstract Object getPropertyValue(Property property);

    public abstract boolean isNull(Property property);

    public Object getPropertyValue(String propertyName) {
        Validate.argumentIsNotNull(propertyName);
        Property property = getGlobalId().getCdoClass().getProperty(propertyName);
        return getPropertyValue(property);
    }

    @Override
    public String toString() {
        return globalId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Cdo)) {
            return false;
        }

        Cdo other = (Cdo) o;
        return  globalId.equals(other.globalId);
    }

    @Override
    public int hashCode() {
        return globalId.hashCode();
    }

}
