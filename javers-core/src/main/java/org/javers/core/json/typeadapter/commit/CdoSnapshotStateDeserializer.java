package org.javers.core.json.typeadapter.commit;

import com.google.gson.*;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.core.metamodel.object.CdoSnapshotStateBuilder;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.*;

import java.lang.reflect.Type;
import java.util.Optional;

import static org.javers.core.metamodel.object.CdoSnapshotStateBuilder.cdoSnapshotState;

/**
 * CdoSnapshotState can't be created by standard {@link CdoSnapshotStateTypeAdapter}
 * due to required managedType
 *
 * @author bartosz walacik
 */
class CdoSnapshotStateDeserializer {
    private final TypeMapper typeMapper;
    private final JsonDeserializationContext context;

    public CdoSnapshotStateDeserializer(TypeMapper typeMapper, JsonDeserializationContext context) {
        this.typeMapper = typeMapper;
        this.context = context;
    }

    public CdoSnapshotState deserialize(JsonElement stateElement, ManagedType managedType){
        Validate.argumentsAreNotNull(stateElement, managedType, context);
        JsonObject stateObject = (JsonObject) stateElement;

        CdoSnapshotStateBuilder builder = cdoSnapshotState();

        stateObject.entrySet().stream().forEach(e -> {
            builder.withPropertyValue(e.getKey(),
                    decodePropertyValue(e.getValue(), context, managedType.findProperty(e.getKey())));

        });

        return builder.build();
    }

    private Object decodePropertyValue(JsonElement propertyElement, JsonDeserializationContext context, Optional<JaversProperty> javersProperty) {

        if (!javersProperty.isPresent()) {
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }

        Type expectedJavaType = typeMapper.getDehydratedType(javersProperty.get().getGenericType());
        JaversType expectedJaversType = javersProperty.get().getType();

        //if primitives on both sides, they should match, otherwise, expectedType is ignored
        if (expectedJaversType instanceof PrimitiveOrValueType) {
            PrimitiveOrValueType expectedJaversPrimitiveType = (PrimitiveOrValueType)expectedJaversType;
            if (expectedJaversPrimitiveType.isJsonPrimitive() &&
                !matches(expectedJaversPrimitiveType, propertyElement)) {
                return decodePropertyValueUsingJsonType(propertyElement, context);
            }
        }

        try {
            return context.deserialize(propertyElement, expectedJavaType);
        } catch (JsonSyntaxException e) {
            // when users's class is refactored, persisted property value
            // can have different type than expected
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }

    }

    private boolean matches(PrimitiveOrValueType javersPrimitive, JsonElement jsonElement) {
        if (!(jsonElement instanceof JsonPrimitive)) {
            return false;
        }

        JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;

        return (jsonPrimitive.isNumber() && javersPrimitive.isNumber()) ||
               (jsonPrimitive.isString() && javersPrimitive.isStringy()) ||
               (jsonPrimitive.isBoolean() && javersPrimitive.isBoolean());

    }

    private Object decodePropertyValueUsingJsonType(JsonElement propertyElement, JsonDeserializationContext context) {
        if (GlobalIdTypeAdapter.looksLikeGlobalId(propertyElement)) {
            return context.deserialize(propertyElement, GlobalId.class);
        }
        return context.deserialize(propertyElement, Object.class);
    }
}