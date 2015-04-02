package org.javers.repository.jql;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.ValueObject;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.type.TypeMapper;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;

/**
* @author bartosz walacik
*/
public final class ValueObjectIdDTO extends GlobalIdDTO {
    private InstanceIdDTO ownerIdDTO;
    private final String fragment;

    ValueObjectIdDTO(Class ownerClass, Object ownerLocalId, String fragment) {
        Validate.argumentsAreNotNull(ownerClass, ownerLocalId, fragment);
        ownerIdDTO = instanceId(ownerLocalId, ownerClass);
        this.fragment = fragment;
    }

    public static ValueObjectIdDTO valueObjectId(Object ownerLocalId, Class ownerClass, String fragment){
        return new ValueObjectIdDTO(ownerClass, ownerLocalId, fragment);
    }

    @Override
    public ValueObjectId create(TypeMapper typeMapper) {
        String voProperty = decodePropertyName();

        InstanceId ownerId = ownerIdDTO.create(typeMapper);

        ValueObject valueObject = typeMapper.getChildValueObject(ownerId.getCdoClass(), voProperty);

        return new ValueObjectId(valueObject, ownerId, fragment);
    }

    @Override
    public String value() {
        return ownerIdDTO.value()+"#"+fragment;
    }


    private String decodePropertyName() {
        String[] parts = fragment.split("/");

        if (parts.length>0){
            return parts[0];
        }
        return "";
    }
}
