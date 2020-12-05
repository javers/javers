package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.InstanceId;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

class InstanceIdFactory {
    private final EntityType entityType;

    InstanceIdFactory(EntityType entityType) {
        this.entityType = entityType;
    }

    InstanceId create(Object localId) {
        Validate.argumentsAreNotNull(entityType, localId);

        Object dehydratedLocalId = dehydratedLocalId(entityType, localId);

        String localIdAsString = localIdAsString(dehydratedLocalId);

        return new InstanceId(entityType.getName(), dehydratedLocalId, localIdAsString);
    }

    InstanceId createFromDehydratedLocalId(Object dehydratedLocalId) {
        Validate.argumentsAreNotNull(entityType, dehydratedLocalId);

        String localIdAsString = localIdAsString(dehydratedLocalId);

        return new InstanceId(entityType.getName(), dehydratedLocalId, localIdAsString);
    }

    String localIdAsString(Object dehydratedLocalId) {
        if (dehydratedLocalId instanceof String) {
            return (String) dehydratedLocalId;
        }

        return localIdAsString(entityType.getIdProperty(), dehydratedLocalId);
    }

    private Object dehydratedLocalId(EntityType entityType, Object localId) {

        if (entityType.hasCompositeId()) {
            Map<String,?> compositeLocalId = (Map)localId;

            return String.join(",", compositeLocalId
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> localIdAsString(entityType.getProperty(e.getKey()), e.getValue()))
                    .collect(Collectors.toList()));
        }

        return dehydratedLocalId(entityType.getIdProperty(), localId);
    }

    String localIdAsString(JaversProperty idProperty, Object dehydratedAtomicLocalId) {
        if (idProperty.isEntityType()) {
            EntityType idPropertyType = idProperty.getType();
            return idPropertyType.getInstanceIdFactory().localIdAsString(dehydratedAtomicLocalId);
        }
        if (idProperty.isValueObjectType()) {
            return dehydratedAtomicLocalId.toString();
        }
        if (idProperty.isPrimitiveOrValueType()) {
            PrimitiveOrValueType primitiveOrValueType = idProperty.getType();
            return primitiveOrValueType.valueToString(dehydratedAtomicLocalId);
        }

        throw idTypeNotSupported();
    }

    private JaversException idTypeNotSupported() {
        return new JaversException(JaversExceptionCode.ID_TYPE_NOT_SUPPORTED,
                entityType.getIdProperty().getType().getClass().getSimpleName(),
                entityType.getIdProperty().getType().getName(),
                entityType.getBaseJavaClass().getName());
    }

    Type getLocalIdDehydratedType() {
        if (entityType.hasCompositeId()) {
            return String.class;
        }

        JaversProperty idProperty = entityType.getIdProperty();

        if (idProperty.isEntityType()) {
            EntityType idPropertyType = idProperty.getType();
            return idPropertyType.getLocalIdDehydratedType();
        }
        if (idProperty.isValueObjectType()) {
            return String.class;
        }
        if (idProperty.isPrimitiveOrValueType()) {
            return idProperty.getGenericType();
        }

        throw idTypeNotSupported();
    }


    private Object dehydratedLocalId(JaversProperty idProperty, Object localId) {
        if (idProperty.isEntityType()) {
            EntityType idPropertyType = idProperty.getType();
            return idPropertyType.getIdOf(localId);
        }
        if (idProperty.isValueObjectType()) {
            ValueObjectType valueObjectType = idProperty.getType();
            return valueObjectType.smartToString(localId);
        }
        if (idProperty.isPrimitiveOrValueType()) {
            return localId;
        }

        throw idTypeNotSupported();
    }
}