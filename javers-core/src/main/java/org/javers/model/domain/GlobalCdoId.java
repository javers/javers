package org.javers.model.domain;

import com.google.gson.annotations.Expose;
import org.javers.common.collections.Objects;
import org.javers.model.mapping.Entity;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

import org.javers.model.mapping.Entity;
import org.omg.CORBA.*;

import java.lang.Object;

/**
 * Client's domain object global ID
 */
public abstract class GlobalCdoId {

    private transient final Entity entity;

    private final Object cdoId;

    protected GlobalCdoId(Object cdoId, Entity entity) {
        argumentsAreNotNull(cdoId, entity);
        this.entity = entity;
        this.cdoId = cdoId;
    }

    public Entity getEntity() {
        return entity;
    }

    /**
     * Identifier of (client's) Entity <b>instance</b>, should be unique in Entity scope
     */
    public Object getCdoId() {
        return cdoId;
    }

    @Override
    public String toString() {
        return entity.getSourceClass().getName()+"/"+cdoId;
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
