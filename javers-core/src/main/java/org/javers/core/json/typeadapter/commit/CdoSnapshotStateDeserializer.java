package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.core.metamodel.object.CdoSnapshotStateBuilder;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.TypeMapper;

import java.lang.reflect.Type;

import static org.javers.core.metamodel.object.CdoSnapshotStateBuilder.cdoSnapshotState;

/**
 * CdoSnapshotState can't be done by standard {@link CdoSnapshotStateTypeAdapter}
 * due to required globalId
 *
 * @author bartosz walacik
 */
public class CdoSnapshotStateDeserializer {
    private final TypeMapper typeMapper;
    private final JsonDeserializationContext context;

    public CdoSnapshotStateDeserializer(TypeMapper typeMapper, JsonDeserializationContext context) {
        this.typeMapper = typeMapper;
        this.context = context;
    }

    public CdoSnapshotState deserialize(JsonElement stateElement, GlobalId globalId){
        Validate.argumentsAreNotNull(stateElement, globalId, context);
        JsonObject stateObject = (JsonObject) stateElement;

        CdoSnapshotStateBuilder builder = cdoSnapshotState();

        for (Property property : globalId.getCdoClass().getProperties()) {
            builder.withPropertyValue(property, decodePropertyValue(stateObject, context, property));
        }

        return builder.build();
    }

    private Object decodePropertyValue(JsonObject element, JsonDeserializationContext context, Property property) {
        JsonElement propertyElement = element.get(property.getName());
        Type dehydratedPropertyType = typeMapper.getDehydratedType(property.getGenericType());
        return context.deserialize(propertyElement, dehydratedPropertyType);
    }
}
