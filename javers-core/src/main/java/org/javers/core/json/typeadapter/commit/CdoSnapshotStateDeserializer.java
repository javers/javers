package org.javers.core.json.typeadapter.commit;

import com.google.gson.*;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.core.metamodel.object.CdoSnapshotStateBuilder;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static org.javers.core.metamodel.object.CdoSnapshotStateBuilder.cdoSnapshotState;

/**
 * CdoSnapshotState can't be created by standard {@link CdoSnapshotStateTypeAdapter}
 * due to required managedType
 *
 * @author bartosz walacik
 */
class CdoSnapshotStateDeserializer {
    private static final Logger logger = LoggerFactory.getLogger(CdoSnapshotStateDeserializer.class);

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

    private Object decodePropertyValue(JsonElement propertyElement, JsonDeserializationContext context, Optional<JaversProperty> javersPropertyOptional) {

        if (!javersPropertyOptional.isPresent()) {
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }

        JaversProperty javersProperty = javersPropertyOptional.get();
        JaversType expectedJaversType = javersProperty.getType();

        // if primitives on both sides, they should match, otherwise, expectedType is ignored
        if (unmatchedPrimitivesOnBothSides(expectedJaversType, propertyElement)) {
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }

        // if collections of primitives on both sides, item types should match,
        // otherwise, item type from expectedType is ignored
        if (shouldUseBareContainerClass(expectedJaversType, propertyElement)) {
            return context.deserialize(propertyElement, ((ContainerType) expectedJaversType).getBaseJavaClass());
        }

        try {
            Type expectedJavaType = typeMapper.getDehydratedType(javersProperty.getGenericType());
            if (javersProperty.getType() instanceof TokenType) {
                return deserializeValueWithTypeGuessing(propertyElement, context);
            } else {
                return context.deserialize(propertyElement, expectedJavaType);
            }
        } catch (JsonSyntaxException | DateTimeParseException e) {
            logger.info("Can't deserialize type-safely the Snapshot property: "+ javersProperty +
                        ". JSON value: "+propertyElement +
                        ". Looks like a type mismatch after refactoring of " + javersProperty.getDeclaringClass().getSimpleName()+
                        " class.");
            // when users's class is refactored, persisted property value
            // can have different type than expected
            return decodePropertyValueUsingJsonType(propertyElement, context);
        }
    }

    private Object deserializeValueWithTypeGuessing(JsonElement propertyElement, JsonDeserializationContext context) {
        if (propertyElement.isJsonPrimitive()){
            JsonPrimitive jsonPrimitive = (JsonPrimitive) propertyElement;

            if (jsonPrimitive.isString()) {
                return jsonPrimitive.getAsString();
            }
            if (jsonPrimitive.isNumber()) {
                if (jsonPrimitive.getAsString().equals(jsonPrimitive.getAsInt()+"")) {
                    return jsonPrimitive.getAsInt();
                }
                if (jsonPrimitive.getAsString().equals(jsonPrimitive.getAsLong()+"")) {
                    return jsonPrimitive.getAsLong();
                }
            }
        }
        return context.deserialize(propertyElement, Object.class);
    }

    private boolean unmatchedPrimitivesOnBothSides(JaversType expectedJaversType, JsonElement propertyElement) {
        if (ifPrimitivesOnBothSides(expectedJaversType, propertyElement)) {
            return !matches((PrimitiveOrValueType)expectedJaversType, (JsonPrimitive) propertyElement);
        }
        return false;
    }

    private boolean ifPrimitivesOnBothSides(JaversType expectedJaversType, JsonElement propertyElement) {
        return expectedJaversType instanceof PrimitiveOrValueType &&
                ((PrimitiveOrValueType) expectedJaversType).isJsonPrimitive() &&
                propertyElement instanceof JsonPrimitive;
    }

    private boolean shouldUseBareContainerClass(JaversType expectedJaversType, JsonElement propertyElement){
        if(!(expectedJaversType instanceof ContainerType) || !(propertyElement instanceof JsonArray)){
            return false;
        }

        ContainerType expectedContainerType = (ContainerType) expectedJaversType;
        JsonArray propertyArray = (JsonArray) propertyElement;

        if (propertyArray.size() == 0) {
            return false;
        }

        JsonElement firstItem = propertyArray.get(0);
        JaversType itemType = typeMapper.getJaversType(expectedContainerType.getItemType());
        return unmatchedPrimitivesOnBothSides(itemType, firstItem);
    }

    private boolean matches(PrimitiveOrValueType javersPrimitive, JsonPrimitive jsonPrimitive) {
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