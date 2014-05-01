package org.javers.core.json.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.GlobalCdoId;

/**
 * @author bartosz walacik
 */
public class GlobalCdoIdTypeAdapter implements JsonTypeAdapter<GlobalCdoId>

{
    @Override
    public GlobalCdoId fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        return null;
    }

    @Override
    public JsonElement toJson(GlobalCdoId sourceValue, JsonSerializationContext jsonSerializationContext) {
        return null;
    }

    @Override
    public Class getValueType() {
        return GlobalCdoId.class;
    }
}
