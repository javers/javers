package org.javers.core.graph;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import java.util.Optional;

/**
 * Abstract holder for client's domain object, {@link EntityType} or {@link ValueObjectType}
 *
 * @author bartosz walacik
 */
public abstract class Cdo {
    private final ManagedType managedType;

    protected Cdo(ManagedType managedType) {
        Validate.argumentsAreNotNull(managedType);
        this.managedType = managedType;
    }

    public abstract GlobalId getGlobalId();

    public abstract Optional<Object> getWrappedCdo();

    public abstract boolean isNull(Property property);

    public abstract Object getPropertyValue(Property property);

    public abstract Object getPropertyValue(String propertyName);

    @Override
    public String toString() {
        return getGlobalId().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cdo)) {
            return false;
        }

        Cdo other = (Cdo) o;
        return  getGlobalId().equals(other.getGlobalId());
    }

    @Override
    public int hashCode() {
        return getGlobalId().hashCode();
    }

    public ManagedType getManagedType() {
        return managedType;
    }
}
