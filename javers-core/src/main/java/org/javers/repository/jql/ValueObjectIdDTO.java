package org.javers.repository.jql;

import org.javers.common.validation.Validate;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;

/**
* @author bartosz walacik
*/
public final class ValueObjectIdDTO extends GlobalIdDTO {
    private final InstanceIdDTO ownerIdDTO;
    private final String path;

    ValueObjectIdDTO(Class ownerClass, Object ownerLocalId, String path) {
        Validate.argumentsAreNotNull(ownerClass, ownerLocalId, path);
        ownerIdDTO = instanceId(ownerLocalId, ownerClass);
        this.path = path;
    }

    public static ValueObjectIdDTO valueObjectId(Object ownerLocalId, Class ownerClass, String fragment){
        return new ValueObjectIdDTO(ownerClass, ownerLocalId, fragment);
    }

    @Override
    public String value() {
        return ownerIdDTO.value()+"#"+ path;
    }

    public InstanceIdDTO getOwnerIdDTO() {
        return ownerIdDTO;
    }

    public String getPath() {
        return path;
    }
}
