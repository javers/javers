package org.javers.guava;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.*;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.validation.Validate;
import org.javers.core.json.JsonAdvancedTypeAdapter;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author bartosz.walacik
 */
class MultisetTypeAdapter implements JsonAdvancedTypeAdapter<Multiset> {

    @Override
    public Multiset fromJson(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonArray jsonArray = (JsonArray) json;

        List<Type> mapTypeArguments = ReflectionUtil.getAllTypeArguments(typeOfT);
        Validate.conditionFulfilled(mapTypeArguments.size() == 1,
                "expected exactly one type parameters in Multiset "+ typeOfT);

        Multiset multiset = HashMultiset.create();

        jsonArray.forEach(e -> {
            Object entry = context.deserialize(e, mapTypeArguments.get(0));
            multiset.add(entry);
        });

        return multiset;
    }

    @Override
    public JsonElement toJson(Multiset sourceValue, Type typeOfT, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();

        sourceValue.forEach(e -> {
            jsonArray.add(context.serialize(e));
        });

        return jsonArray;
    }

    @Override
    public Class getTypeSuperclass() {
        return Multiset.class;
    }
}
