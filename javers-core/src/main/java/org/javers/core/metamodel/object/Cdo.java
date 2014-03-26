package org.javers.core.metamodel.object;

import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.ValueObject;

import static org.javers.common.validation.Validate.*;

/**
 * Abstract holder for client's domain object, {@link Entity} or {@link ValueObject}
 *
 * @author bartosz walacik
 */
public abstract class Cdo {
    private final GlobalCdoId globalId;

    protected Cdo(GlobalCdoId globalId) {
        this.globalId = globalId;
    }

    public GlobalCdoId getGlobalId() {
        return globalId;
    }

    public Object getLocalId() {
        return globalId.getCdoId();
    }

    /**
     * shortcut to {@link GlobalCdoId#getCdoClass()}
     */
    public ManagedClass getManagedClass() {
        return globalId.getCdoClass();
    }

    public abstract Object getWrappedCdo();

    public abstract Object getPropertyValue(Property property);

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
