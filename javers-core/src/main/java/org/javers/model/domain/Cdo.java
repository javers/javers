package org.javers.model.domain;

import org.javers.common.validation.Validate;
import org.javers.model.mapping.Entity;

import static org.javers.common.validation.Validate.*;

/**
 * Holder for Client's domain object
 *
 * @author bartosz walacik
 */
public class Cdo {
    private final Object wrappedCdo;
    private final GlobalCdoId globalId;

    public Cdo(Object cdo, Entity entity) {
        argumentIsNotNull(cdo);
        argumentIsNotNull(entity);
        argumentCheck(entity.isInstance(cdo), "cdo is not an instance of entity");
        this.wrappedCdo = cdo;
        this.globalId = new GlobalCdoId(entity.getCdoIdOf(cdo),entity);
    }

    public Object getWrappedCdo() {
        return wrappedCdo;
    }

    public GlobalCdoId getGlobalId() {
        return globalId;
    }

    public Object getLocalId() {
        return globalId.getLocalCdoId();
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
