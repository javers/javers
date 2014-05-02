package org.javers.core.json.typeadapter;

import com.google.gson.*;
import org.javers.common.collections.Lists;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.json.JsonTypeAdapter;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class AtomicTypeAdapter implements JsonTypeAdapter<Atomic> {

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
    public List<Class> getValueTypes() {
        return (List)Lists.immutableListOf(Atomic.class);
    }
}
