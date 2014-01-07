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
public class GlobalCdoId {

    private transient final Entity entity;

    private final Object cdoId;

    private final String fragment;

    /**
     * creates Entity instance identifier
     *
     * @param cdoId see {@link #getCdoId()}
     */
    public GlobalCdoId(Object cdoId, Entity entity) {
        argumentsAreNotNull(cdoId, entity);

        this.entity = entity;
        this.cdoId = cdoId;
        this.fragment = "";
    }

    /**
     * creates ValueObject identifier
     *
     * @param cdoId see {@link #getCdoId()}
     * @param fragment see {@link #getFragment()}
     */
    public GlobalCdoId(Object cdoId, Entity entity, String fragment) {
        argumentsAreNotNull(cdoId, entity, fragment);

        this.entity = entity;
        this.cdoId = cdoId;
        this.fragment = fragment;
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

    /**
     * Identifier of (client's) ValueObject, should be unique in Entity <b>instance</b> scope
     */
    public String getFragment() {
        return fragment;
    }

    @Override
    public String toString() {
        if (hasFragment()) {
            return entity.getSourceClass().getName()+"/"+cdoId+"#"+fragment;
        } else {
            return entity.getSourceClass().getName()+"/"+cdoId;
        }
    }

    private boolean hasFragment() {
        return fragment.length() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null) { return false;}
        if (!(o instanceof GlobalCdoId)) {return false;}

        GlobalCdoId other = (GlobalCdoId) o;
        return (entity.equals(other.entity) && cdoId.equals(other.cdoId)
                                            && fragment.equals(other.fragment));

    }

    @Override
    public int hashCode() {
        return entity.hashCode() + cdoId.hashCode() + fragment.hashCode();
    }
}
