package org.javers.core.json.typeadapter.change;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.diff.changetype.PropertyChangeMetadata;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.CollectionChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ElementValueChange;
import org.javers.core.diff.changetype.container.ValueAddOrRemove;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.metamodel.type.ContainerType;
import org.javers.core.metamodel.type.TypeMapper;

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
    public static final String LEFT_VALUE = "leftValue";
    public static final String RIGHT_VALUE = "rightValue";

    public ContainerChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public T fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeMetadata stub = deserializeStub(jsonObject, context);

        ContainerType containerType = getJaversProperty(stub).getType();

        List<ContainerElementChange> changes = parseChanges(jsonObject, context, containerType);

        List<Object> leftValue = new ArrayList<>(parseValue(jsonObject.getAsJsonArray(LEFT_VALUE),context,containerType));
        List<Object> rightValue = new ArrayList<>(parseValue(jsonObject.getAsJsonArray(RIGHT_VALUE),context,containerType));

        return (T) newInstance(stub, changes, leftValue, rightValue);
    }

    private List<Object>parseValue(JsonArray array,JsonDeserializationContext context, ContainerType containerType){
        List<Object> list = new ArrayList<>();
        for (JsonElement e : array) {
            JsonObject elementChange = (JsonObject) e;
            Object value = deserializeForExpectedType(elementChange, context, VALUE_FIELD, containerType.getItemType());
            list.add(value);
        }
        return list;
    }

    protected abstract ContainerChange<?> newInstance(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, List<Object> leftValue,
        List<Object> rightValue);

    private List<ContainerElementChange> parseChanges(JsonObject jsonObject, JsonDeserializationContext context, ContainerType containerType) {
        List<ContainerElementChange> result = new ArrayList<>();

        JsonArray array = jsonObject.getAsJsonArray(CHANGES_FIELD);

        for (JsonElement e : array) {
            JsonObject elementChange = (JsonObject) e;
            String elementChangeType = elementChange.get(ELEMENT_CHANGE_TYPE_FIELD).getAsString();

            if (ValueAdded.class.getSimpleName().equals(elementChangeType)) {
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

    private ElementValueChange parseElementValueChange(JsonObject elementChange, JsonDeserializationContext context, ContainerType containerType) {
        Object lValue = decodeValue(elementChange, context, LEFT_VALUE_FIELD, containerType.getItemType());
        Object rValue = decodeValue(elementChange, context, RIGHT_VALUE_FIELD, containerType.getItemType());
        return new ElementValueChange(parseIndex(elementChange), lValue, rValue);
    }

    private ValueAdded parseValueAdded(JsonObject elementChange, JsonDeserializationContext context, ContainerType containerType) {
        Object value = decodeValue(elementChange, context, VALUE_FIELD, containerType.getItemClass());

        Integer idx = parseIndex(elementChange);
        if (idx != null) {
            return new ValueAdded(idx, value);
        } else {
            return new ValueAdded(value);
        }
    }

    private ValueRemoved parseValueRemoved(JsonObject elementChange, JsonDeserializationContext context, ContainerType containerType) {
        Object value = decodeValue(elementChange, context, VALUE_FIELD, containerType.getItemClass());
        Integer idx = parseIndex(elementChange);
        if (idx != null) {
            return new ValueRemoved(idx, value);
        } else {
            return new ValueRemoved(value);
        }
    }

    private Integer parseIndex(JsonObject elementChange) {
        if (!elementChange.has(INDEX_FIELD) || elementChange.get(INDEX_FIELD).isJsonNull()) {
            return null;
        }
        return elementChange.get(INDEX_FIELD).getAsInt();
    }

    private Object deserializeForExpectedType(JsonObject elementChange, JsonDeserializationContext context, String fieldName, Type expectedType) {
        return context.deserialize(elementChange.get(fieldName), expectedType);
    }

    private Object decodeValue(JsonObject elementChange, JsonDeserializationContext context, String fieldName, Type expectedType) {
        return context.deserialize(elementChange.get(fieldName), typeMapper.getDehydratedType(expectedType));
    }

    @Override
    public JsonElement toJson(T change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        appendBody(change, jsonObject, context);

        return jsonObject;
    }

    private void appendBody(ContainerChange<?> change, JsonObject toJson, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();
        JsonArray leftValueArray = new JsonArray();
        JsonArray rightValueArray = new JsonArray();
        if(change instanceof CollectionChange) {
            for (Object obj : ((CollectionChange<?>)change).getLeft()){
                appendListValue(context, leftValueArray, obj);
            }
            for (Object obj : ((CollectionChange<?>)change).getRight()){
                appendListValue(context, rightValueArray, obj);
            }
        }else{
            for (Object obj : ((ArrayChange)change).getLeft()){
                appendListValue(context, leftValueArray, obj);
            }
            for (Object obj : ((ArrayChange)change).getRight()){
                appendListValue(context, rightValueArray, obj);
            }
        }
        toJson.add(LEFT_VALUE, leftValueArray);
        toJson.add(RIGHT_VALUE, rightValueArray);

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

    private void appendListValue(JsonSerializationContext context, JsonArray leftValueArray, Object obj) {
        JsonObject leftValue = new JsonObject();
        leftValue.add(VALUE_FIELD, context.serialize(obj));
        leftValueArray.add(leftValue);
    }

}
