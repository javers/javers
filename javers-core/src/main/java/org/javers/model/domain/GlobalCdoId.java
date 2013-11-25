package org.javers.model.domain;

import org.javers.model.mapping.Entity;

import static org.javers.common.validation.Validate.argumentIsNotNull;

import org.javers.model.mapping.Entity;

/**
 * Holder for client's domain object global ID
 */
public class GlobalCdoId {
    private final Entity entity;
    private final Object cdoId;

    /**
     * @param cdoId Identifier of client's domain objects, should be unique in Entity scope
     */
    public GlobalCdoId(Object cdoId, Entity entity) {
        argumentIsNotNull(cdoId);
        argumentIsNotNull(entity);

        this.entity = entity;
        this.cdoId = cdoId;
    }

    public Entity getEntity() {
        return entity;
    }

    public Object getLocalCdoId() {
        return cdoId;
    }

    @Override
    public String toString() {
        return entity.getSourceClass().getName()+"#"+cdoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null) { return false;}
        if (!(o instanceof GlobalCdoId)) {return false;}

        GlobalCdoId other = (GlobalCdoId) o;
        return (entity.equals(other.entity) && cdoId.equals(other.cdoId));
    }

    @Override
    public int hashCode() {
        return entity.hashCode() + cdoId.hashCode();
    }
}
