package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.core.metamodel.property.Property;

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
        throw new UnsupportedOperationException("use CdoSnapshotStateSerializer");
    }

    @Override
    public JsonElement toJson(CdoSnapshotState snapshotState, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Property property : snapshotState.getProperties()) {
            jsonObject.add(property.getName(), context.serialize(snapshotState.getPropertyValue(property)));
        }

        return jsonObject;
    }
}
