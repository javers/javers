package org.javers.core.metamodel.object;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.type.*;
import java.lang.reflect.Type;

class InstanceIdFactory {

    InstanceId create(EntityType entityType, Object localId) {
        Validate.argumentsAreNotNull(entityType, localId);

        Object dehydratedLocalId = dehydratedLocalId(entityType, localId);

        String localIdAsString = localIdAsString(entityType, dehydratedLocalId);

        return new InstanceId(entityType.getName(), dehydratedLocalId, localIdAsString);
    }

    InstanceId createFromDehydratedLocalId(EntityType entityType, Object dehydratedLocalId) {
        Validate.argumentsAreNotNull(entityType, dehydratedLocalId);

        String localIdAsString = localIdAsString(entityType, dehydratedLocalId);

        return new InstanceId(entityType.getName(), dehydratedLocalId, localIdAsString);
    }

    private boolean isIdEntity(EntityType entityType) {
        return entityType.getIdProperty().getType() instanceof EntityType;
    }

    private boolean isIdValueObject(EntityType entityType) {
        return entityType.getIdProperty().getType() instanceof ValueObjectType;
    }

    private boolean isIdPrimitiveOrValue(EntityType entityType) {
        return entityType.getIdProperty().getType() instanceof PrimitiveOrValueType;
    }

    private String localIdAsString(EntityType entityType, Object dehydratedLocalId) {
        if (isIdEntity(entityType)) {
            EntityType idPropertyType = entityType.getIdPropertyType();
            return localIdAsString(idPropertyType, dehydratedLocalId);
        }

        if (isIdValueObject(entityType)) {
            return dehydratedLocalId.toString();
        }
        if (isIdPrimitiveOrValue(entityType)) {
            PrimitiveOrValueType primitiveOrValueType = entityType.getIdProperty().getType();
            return primitiveOrValueType.smartToString(dehydratedLocalId);
        }

        //TODO throw
        throw new RuntimeException("//TODO");
    }

    Type getLocalIdDehydratedType(EntityType entityType) {
        if (isIdEntity(entityType)) {
            EntityType idPropertyType = entityType.getIdPropertyType();
            return idPropertyType.getIdPropertyGenericType();
        }
        if (isIdValueObject(entityType)) {
            return String.class;
        }
        if (isIdPrimitiveOrValue(entityType)) {
            return entityType.getIdPropertyGenericType();
        }

        //TODO throw
        throw new RuntimeException("//TODO");
    }

    private Object dehydratedLocalId(EntityType entityType, Object localId) {
        if (isIdEntity(entityType)) {
            EntityType idPropertyType = entityType.getIdPropertyType();
            return idPropertyType.getIdOf(localId);
        }
        if (isIdValueObject(entityType)) {
            ValueObjectType valueObjectType = entityType.getIdPropertyType();
            return valueObjectType.smartToString(localId);
        }
        if (isIdPrimitiveOrValue(entityType)) {
            return localId;
        }

        //TODO throw
        throw new RuntimeException("//TODO");
    }
}
