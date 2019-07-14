package org.javers.core.json.typeadapter.change;

import com.google.gson.*;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.PropertyChangeMetadata;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.metamodel.type.ContainerType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz walacik
 */
abstract class ContainerChangeTypeAdapter<T extends ContainerChange> extends ChangeTypeAdapter<T> {
    private static final String CHANGES_FIELD = "elementChanges";
    private static final String ELEMENT_CHANGE_TYPE_FIELD = "elementChangeType";
    private static final String INDEX_FIELD = "index";
    private static final String VALUE_FIELD = "value";
    private static final String LEFT_VALUE_FIELD = "leftValue";
    private static final String RIGHT_VALUE_FIELD = "rightValue";

    public ContainerChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public T fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeMetadata stub = deserializeStub(jsonObject, context);

        ContainerType containerType = getJaversProperty(stub).getType();

        List<ContainerElementChange> changes = parseChanges(jsonObject, context, containerType);

        return (T) newInstance(stub, changes);
    }

    protected abstract ContainerChange newInstance(PropertyChangeMetadata metadata, List<ContainerElementChange> changes);

    private List<ContainerElementChange> parseChanges(JsonObject jsonObject, JsonDeserializationContext context, ContainerType containerType) {
        List<ContainerElementChange> result = new ArrayList<>();

        JsonArray array = jsonObject.getAsJsonArray(CHANGES_FIELD);

        for (JsonElement e : array){
            JsonObject elementChange = (JsonObject)e;
            String elementChangeType  = elementChange.get(ELEMENT_CHANGE_TYPE_FIELD).getAsString();

            if (ValueAdded.class.getSimpleName().equals(elementChangeType)){
                result.add(parseValueAdded(elementChange, context, containerType));
            } else if (ValueRemoved.class.getSimpleName().equals(elementChangeType)) {
                result.add(parseValueRemoved(elementChange, context, containerType));
            } else if (ElementValueChange.class.getSimpleName().equals(elementChangeType)) {
                result.add(parseElementValueChange(elementChange, context, containerType));
            } else {
                throw new JaversException(JaversExceptionCode.MALFORMED_ENTRY_CHANGE_TYPE_FIELD, containerType);
            }
        }

        return result;
    }

    private ElementValueChange parseElementValueChange(JsonObject elementChange, JsonDeserializationContext context, ContainerType containerType){
        Object lValue = decodeValue(elementChange, context, LEFT_VALUE_FIELD, containerType.getItemType());
        Object rValue = decodeValue(elementChange, context, RIGHT_VALUE_FIELD, containerType.getItemType());
        return new ElementValueChange(parseIndex(elementChange), lValue, rValue);
    }

    private ValueAdded parseValueAdded(JsonObject elementChange, JsonDeserializationContext context, ContainerType containerType){
        Object value = decodeValue(elementChange, context, VALUE_FIELD, containerType.getItemClass());

        Integer idx = parseIndex(elementChange);
        if (idx != null) {
            return new ValueAdded(idx, value);
        } else {
            return new ValueAdded(value);
        }
    }

    private ValueRemoved parseValueRemoved(JsonObject elementChange, JsonDeserializationContext context, ContainerType containerType){
        Object value = decodeValue(elementChange, context, VALUE_FIELD, containerType.getItemClass());
        Integer idx = parseIndex(elementChange);
        if (idx != null) {
            return new ValueRemoved(idx, value);
        } else {
            return new ValueRemoved(value);
        }
    }

    private Integer parseIndex(JsonObject elementChange) {
        if (!elementChange.has(INDEX_FIELD) || elementChange.get(INDEX_FIELD).isJsonNull()){
            return null;
        }
        return elementChange.get(INDEX_FIELD).getAsInt();
    }

    private Object decodeValue(JsonObject elementChange, JsonDeserializationContext context, String fieldName, Type expectedType){
        return context.deserialize(elementChange.get(fieldName), typeMapper.getDehydratedType(expectedType));
    }

    @Override
    public JsonElement toJson(T change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        appendBody(change, jsonObject, context);

        return jsonObject;
    }

    private void appendBody(ContainerChange change, JsonObject toJson, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();

        for (ContainerElementChange elementChange : change.getChanges()) {
            JsonObject jsonElement = new JsonObject();
            jsonElement.addProperty(ELEMENT_CHANGE_TYPE_FIELD, elementChange.getClass().getSimpleName());

            jsonElement.addProperty(INDEX_FIELD, elementChange.getIndex());

            if (elementChange instanceof ValueAddOrRemove) {
                ValueAddOrRemove valueAddOrRemove = (ValueAddOrRemove) elementChange;

                jsonElement.add(VALUE_FIELD, context.serialize(valueAddOrRemove.getValue()));
            }

            if (elementChange instanceof ElementValueChange) {
                ElementValueChange elementValueChange = (ElementValueChange) elementChange;

                jsonElement.add(LEFT_VALUE_FIELD, context.serialize(elementValueChange.getLeftValue()));
                jsonElement.add(RIGHT_VALUE_FIELD, context.serialize(elementValueChange.getRightValue()));
            }
            jsonArray.add(jsonElement);
        }
        toJson.add(CHANGES_FIELD, jsonArray);
    }

}
