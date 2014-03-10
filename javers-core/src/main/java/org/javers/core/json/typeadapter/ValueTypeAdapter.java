package org.javers.core.json.typeadapter;

import com.google.gson.*;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.Value;
import org.javers.core.json.JsonTypeAdapter;

/**
 * @author bartosz walacik
 */
public class ValueTypeAdapter implements JsonTypeAdapter<Value> {

    private final boolean typeSafety;

    public ValueTypeAdapter(boolean typeSafety) {
        this.typeSafety = typeSafety;
    }

    @Override
    public JsonElement toJson(Value sourceValue, JsonSerializationContext jsonSerializationContext) {
        if (sourceValue.isNull()) {
            return JsonNull.INSTANCE;
        }

        JsonElement rawValue = jsonSerializationContext.serialize(sourceValue.unwrap());

        if (sourceValue.isJsonBasicType()|| !typeSafety){
            return rawValue;
        }

        return wrapTypeSafely(sourceValue, jsonSerializationContext);
    }

    private JsonElement wrapTypeSafely(Value sourceValue, JsonSerializationContext jsonSerializationContext){
        JsonObject element = new JsonObject();
        element.addProperty("typeAlias", sourceValue.unwrap().getClass().getSimpleName());
        element.add("value" ,  jsonSerializationContext.serialize(sourceValue.unwrap()));
        return element;
    }

    @Override
    public Value fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Class getValueType() {
        return Value.class;
    }
}
