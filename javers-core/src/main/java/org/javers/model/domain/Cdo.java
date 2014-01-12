package org.javers.model.domain;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.Entity;

import static org.javers.common.validation.Validate.*;

/**
 * Holder for client's domain object,
 * extracts its {@link GlobalCdoId}
 *
 * @author bartosz walacik
 */
public class Cdo {
    private final Object wrappedCdo;
    private final GlobalCdoId globalId;

    /**
     * Creates wrapper for Entity instance
     */
    public Cdo(Object cdo, Entity entity) {
        argumentIsNotNull(cdo);
        argumentIsNotNull(entity);
        argumentCheck(entity.isInstance(cdo), "cdo is not an instance of given entity");

        Object cdoId = entity.getIdOf(cdo);
        if (cdoId == null) {
            throw new JaversException(JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID, entity.getClass().getName());
        }

        this.wrappedCdo = cdo;
        this.globalId = new InstanceId(cdoId,entity);
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

    public Entity getEntity() {
        return globalId.getEntity();
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
