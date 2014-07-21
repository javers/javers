package org.javers.core.metamodel.object;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.type.TypeMapper;

/**
* @author bartosz walacik
*/
public class InstanceIdDTO extends GlobalIdDTO {
    private final Class  javaClass;
    private final Object localId;

    InstanceIdDTO(Class javaClass, Object localId) {
        Validate.argumentsAreNotNull(javaClass, localId);
        this.javaClass = javaClass;
        this.localId = localId;
    }

    public static InstanceIdDTO instanceId(Object localId, Class javaClass){
        return new InstanceIdDTO(javaClass,localId);
    }

    @Override
    public InstanceId create(TypeMapper typeMapper) {
        return InstanceId.createFromId(localId, typeMapper.getManagedClass(javaClass, Entity.class));
    }

    @Override
    public String value() {
        return javaClass.getName()+"/"+localId;
    }

    public Class getEntity() {
        return javaClass;
    }

    public Object getCdoId() {
        return localId;
    }
}
