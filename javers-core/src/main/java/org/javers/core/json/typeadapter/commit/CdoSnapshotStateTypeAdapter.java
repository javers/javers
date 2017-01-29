package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.metamodel.object.CdoSnapshotState;

/**
 * @author bartosz walacik
 */
public class CdoSnapshotStateTypeAdapter extends JsonTypeAdapterTemplate<CdoSnapshotState> {

    @Override
    public Class getValueType() {
        return CdoSnapshotState.class;
    }

    @Override
    public CdoSnapshotState fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        throw new UnsupportedOperationException("use CdoSnapshotStateDeserializer");
    }

    @Override
    public JsonElement toJson(CdoSnapshotState snapshotState, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        snapshotState.forEachProperty( (pName, pValue) -> jsonObject.add(pName, context.serialize(pValue)));
        return jsonObject;
    }
}
