package org.javers.core.metamodel.object;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import java.util.Optional;

/**
 * Abstract holder for client's domain object, {@link EntityType} or {@link ValueObjectType}
 *
 * @author bartosz walacik
 */
public abstract class Cdo {
    private final GlobalId globalId;
    private final ManagedType managedType;

    protected Cdo(GlobalId globalId, ManagedType managedType) {
        Validate.argumentsAreNotNull(globalId, managedType);
        this.globalId = globalId;
        this.managedType = managedType;
    }

    public GlobalId getGlobalId() {
        return globalId;
    }

    public abstract Optional<Object> getWrappedCdo();

    public abstract boolean isNull(Property property);

    public abstract Object getPropertyValue(Property property);

    public abstract Object getPropertyValue(String propertyName);

    @Override
    public String toString() {
        return globalId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cdo)) {
            return false;
        }

        Cdo other = (Cdo) o;
        return  globalId.equals(other.globalId);
    }

    @Override
    public int hashCode() {
        return globalId.hashCode();
    }

    public ManagedType getManagedType() {
        return managedType;
    }
}
