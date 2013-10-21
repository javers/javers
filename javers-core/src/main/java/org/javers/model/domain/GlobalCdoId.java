package org.javers.model.domain;

import static org.javers.common.validation.Validate.argumentIsNotNull;

import org.javers.model.mapping.Entity;

/**
 * Clients domain object global ID <br/>
 * <p/>
 * Immutable
 */
public class GlobalCdoId {
    private final Entity entity;
    /**
     * Identifiers of client's domain objects should be unique in Entity scope
     */
    private final Object cdoId;

    public GlobalCdoId(Entity entity, Object cdoId) {
        argumentIsNotNull(cdoId);
        argumentIsNotNull(entity);

        this.entity = entity;
        this.cdoId = cdoId;
    }

    public Entity getEntity() {
        return entity;
    }

    public Object getCdoId() {
        return cdoId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        GlobalCdoId other = (GlobalCdoId) obj;
        return cdoId.equals(other.cdoId) && entity.getSourceClass().equals(other.entity.getSourceClass());
    }

    @Override
    public int hashCode() {
        int result = entity.getSourceClass().hashCode();
        result = 31 * result + cdoId.hashCode();
        return result;
    }
}
