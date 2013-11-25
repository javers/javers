package org.javers.model.domain;

import org.javers.model.mapping.Entity;

import static org.javers.common.validation.Validate.argumentIsNotNull;

import org.javers.model.mapping.Entity;

/**
 * Clients domain object global ID
 * <br/>
 *
 * Immutable
 */
public class GlobalCdoId {
    private final Entity entity;
    /**
     * Identifiers of client's domain objects should be unique in Entity scope
     */
    private final Object cdoId;

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
}
