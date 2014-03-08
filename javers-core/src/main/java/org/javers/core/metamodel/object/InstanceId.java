package org.javers.core.metamodel.object;

import org.javers.common.exception.exceptions.JaversException;
import org.javers.core.metamodel.property.Entity;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Entity instance global identifier, consists of Entity reference and cdoId
 *
 * @author bartosz walacik
 */
public class InstanceId extends GlobalCdoId {
    private transient final Entity entity;
    private final Object cdoId;

    /**
     * @throws JaversException ENTITY_INSTANCE_WITH_NULL_ID
     * @throws JaversException NOT_INSTANCE_OF
     */
    public InstanceId(Object instance, Entity entity) {
        argumentsAreNotNull(instance, entity);
        this.entity = entity;
        this.cdoId = entity.getIdOf(instance);
    }

    @Override
    public Entity getCdoClass() {
        return entity;
    }

    /**
     * Identifier of (client's) Entity <b>instance</b>, should be unique in Entity scope
     */
    @Override
    public Object getCdoId() {
        return cdoId;
    }

    @Override
    public String toString() {
        return entity.getSourceClass().getName()+"/"+cdoId;
    }

    public boolean idEquals(Object instance) {
        if (instance == null) {
            return false;
        }
        if (!entity.getSourceClass().isAssignableFrom(instance.getClass())){
            return false;
        }

        return cdoId.equals(entity.getIdOf(instance));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || !(o instanceof InstanceId)) {return false;}

        InstanceId other = (InstanceId) o;
        return (entity.equals(other.entity) && cdoId.equals(other.cdoId));
    }

    @Override
    public int hashCode() {
        return entity.hashCode() + cdoId.hashCode();
    }
}
