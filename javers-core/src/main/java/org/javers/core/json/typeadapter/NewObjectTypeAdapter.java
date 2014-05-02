package org.javers.core.json.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javers.common.collections.Optional;
import org.javers.core.diff.changetype.NewObject;

public class NewObjectTypeAdapter extends ChangeTypeAdapter<NewObject> {

    @Override
    public NewObject fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        return new NewObject(deserializeAffectedCdoId(jsonObject,context), Optional.empty());
    }

    @Override
    public Class getValueType() {
        return NewObject.class;
    }
}
