package org.javers.core.metamodel.object;

import org.javers.common.exception.JaversException;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.type.EntityType;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Entity instance global identifier, consists of Entity reference and cdoId
 *
 * @author bartosz walacik
 */
public class InstanceId extends GlobalId {
    private transient final EntityType entity;
    private final Object cdoId;

    private InstanceId(Object cdoId, EntityType entity) {
        argumentsAreNotNull(cdoId, entity);
        this.entity = entity;
        this.cdoId = cdoId;
    }

    /**
     * @throws JaversException ENTITY_INSTANCE_WITH_NULL_ID
     * @throws JaversException NOT_INSTANCE_OF
     */
    public static InstanceId createFromInstance(Object instance, EntityType entity){
        return new InstanceId(entity.getIdOf(instance), entity);
    }

    public static InstanceId createFromId(Object id, EntityType entity){
        return new InstanceId(id, entity);
    }

    @Override
    public EntityType getManagedType() {
        return entity;
    }

    /**
     * Identifier of (client's) Entity <b>instance</b>, should be unique in Entity scope.
     * For example database primary key or any domain identifier like user.login
     */
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
