package org.javers.core.json;

import com.google.gson.*;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.json.JsonTypeAdapterTemplate;

/**
 * @author bartosz walacik
 */
class AtomicTypeAdapter extends JsonTypeAdapterTemplate<Atomic> {

    private final boolean typeSafety;

    public AtomicTypeAdapter(boolean typeSafety) {
        this.typeSafety = typeSafety;
    }

    @Override
    public JsonElement toJson(Atomic sourceValue, JsonSerializationContext jsonSerializationContext) {
        if (sourceValue.isNull()) {
            return JsonNull.INSTANCE;
        }

        JsonElement rawValue = jsonSerializationContext.serialize(sourceValue.unwrap());

        if (sourceValue.isJsonBasicType()|| !typeSafety){
            return rawValue;
        }

        return wrapTypeSafely(sourceValue, jsonSerializationContext);
    }

    private JsonElement wrapTypeSafely(Atomic sourceValue, JsonSerializationContext jsonSerializationContext){
        JsonObject element = new JsonObject();
        element.addProperty("typeAlias", sourceValue.unwrap().getClass().getSimpleName());
        element.add("value" ,  jsonSerializationContext.serialize(sourceValue.unwrap()));
        return element;
    }

    @Override
    public Atomic fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public Class getValueType() {
        return Atomic.class;
    }
}
