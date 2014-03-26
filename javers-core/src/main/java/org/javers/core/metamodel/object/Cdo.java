package org.javers.core.metamodel.object;

import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.ValueObject;

import static org.javers.common.validation.Validate.*;

/**
 * Holder for client's domain object, {@link Entity} or {@link ValueObject}
 *
 * @author bartosz walacik
 */
public class Cdo {
    private final Object wrappedCdo;
    private final GlobalCdoId globalId;

    /**
     * Creates wrapper for Entity instance
     */
    public Cdo(Object wrappedCdo, GlobalCdoId globalId) {
        argumentsAreNotNull(wrappedCdo, globalId);
        argumentCheck(globalId.getCdoClass().isInstance(wrappedCdo), "wrappedCdo is not an instance of given managedClass");

        this.globalId = globalId;
        this.wrappedCdo = wrappedCdo;
    }

    /**
     * never returns null
     */
    public Object getWrappedCdo() {
        return wrappedCdo;
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

    @Override
    public String toString() {
        return globalId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null) { return false;}
        if (!(o instanceof Cdo)) {return false;}

        Cdo other = (Cdo) o;
        return  globalId.equals(other.globalId);
    }

    @Override
    public int hashCode() {
        return globalId.hashCode();
    }
}
