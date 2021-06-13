package org.javers.java8support;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.json.JsonAdvancedTypeAdapter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class OptionalTypeAdapter implements JsonAdvancedTypeAdapter<Optional> {

    @Override
    public Optional fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext jsonDeserializationContext) {
        if ( json.isJsonObject() ) {
            JsonObject jsonObject = (JsonObject)json;

            List<Type> elementTypes = ReflectionUtil.getAllTypeArguments(typeOfT);
            Type elementType = null;
            if (elementTypes.size() == 0) {
                elementType = new TypeToken<Object>(){}.getType();
            }else {
                elementType = elementTypes.get(0);
            }

            return Optional.ofNullable(jsonDeserializationContext.deserialize(jsonObject.get("value"), elementType));
        }
        return null;
    }

    @Override
    public JsonElement toJson(Optional sourceValue, Type typeOfT, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.add("value", jsonSerializationContext.serialize(sourceValue.orElse(null)));
        return json;
    }

    @Override
    public Class<Optional> getTypeSuperclass() {
        return Optional.class;
    }
}
