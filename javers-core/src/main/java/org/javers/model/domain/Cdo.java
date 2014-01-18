package org.javers.model.domain;

import org.javers.model.mapping.Entity;
import org.javers.model.mapping.ManagedClass;
import org.javers.model.mapping.ValueObject;

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

        //Object cdoId = entity.getIdOf(cdo);
        //if (cdoId == null) {
        //    throw new JaversException(JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID, entity.getClass().getName());
        //}
        //new InstanceId(cdoId, entity);

        this.globalId = globalId;
        this.wrappedCdo = wrappedCdo;

    }

    /**
     * Creates wrapper for ValueObject instance
    public Cdo(InstanceId owningEntityInstanceId, Property location, Object valueObject) {
        argumentsAreNotNull(owningEntityInstanceId, location, valueObject);
        this.globalId = new ValueObjectId(owningEntityInstanceId,location.getName());
        this.wrappedCdo = valueObject;
    }*/

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
        return ( globalId.equals(other.globalId));
    }

    @Override
    public int hashCode() {
        return globalId.hashCode();
    }
}
