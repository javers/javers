package org.javers.core.metamodel.object;

import org.javers.common.exception.JaversException;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.clazz.Entity;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Entity instance global identifier, consists of Entity reference and cdoId
 *
 * @author bartosz walacik
 */
public class InstanceId extends GlobalId {
    private transient final Entity entity;
    private final Object cdoId;

    private InstanceId(Object cdoId, Entity entity) {
        argumentsAreNotNull(cdoId, entity);
        this.entity = entity;
        this.cdoId = cdoId;
    }

    /**
     * @throws JaversException ENTITY_INSTANCE_WITH_NULL_ID
     * @throws JaversException NOT_INSTANCE_OF
     */
    public static InstanceId createFromInstance(Object instance, Entity entity){
        return new InstanceId(entity.getIdOf(instance), entity);
    }

    public static InstanceId createFromId(Object id, Entity entity){
        return new InstanceId(id, entity);
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

    public String value() {
        return entity.getName()+"/"+getCdoIdAsString();
    }

    public boolean idEquals(Object instance) {
        if (instance == null) {
            return false;
        }
        if (!entity.isInstance(instance)){
            return false;
        }

        return cdoId.equals(entity.getIdOf(instance));
    }

    private String getCdoIdAsString(){
        return ReflectionUtil.reflectiveToString(cdoId);
    }
}
