package org.javers.guava;

import com.google.common.collect.*;
import com.google.gson.*;
import org.javers.common.collections.Lists;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.validation.Validate;
import org.javers.core.json.JsonAdvancedTypeAdapter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author bartosz.walacik
 */
class MultimapTypeAdapter implements JsonAdvancedTypeAdapter<Multimap> {
    @Override
    public Multimap fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonArray jsonArray = (JsonArray) json;

        List<Type> mapTypeArguments = ReflectionUtil.getAllTypeArguments(typeOfT);
        Validate.conditionFulfilled(mapTypeArguments.size() == 2,
                                    "expected exactly two type parameters in Multimap "+ typeOfT);

        Multimap multimap = ArrayListMultimap.create();

        jsonArray.forEach(e -> {
            JsonObject entry = e.getAsJsonObject();
            Object key = context.deserialize(entry.get("key"), mapTypeArguments.get(0));
            Object value = context.deserialize(entry.get("value"), mapTypeArguments.get(1));
            multimap.put(key, value);
        });

        return multimap;
    }

    @Override
    public JsonElement toJson(Multimap sourceValue, Type typeOfT, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();

        sourceValue.entries().forEach(e -> {
            JsonObject entry = new JsonObject();
            entry.add("key", context.serialize(((Map.Entry)e).getKey()));
            entry.add("value", context.serialize(((Map.Entry)e).getValue()));
            jsonArray.add(entry);
        });

        return jsonArray;
    }

    @Override
    public Class getTypeSuperclass() {
        return Multimap.class;
    }
}
