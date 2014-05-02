package org.javers.core.json.typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.changetype.ReferenceChange;

public class ReferenceChangeTypeAdapter extends ChangeTypeAdapter<ReferenceChange> {

    @Override
    public JsonElement toJson(ReferenceChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add("leftReference",  context.serialize(change.getLeftReference()));
        jsonObject.add("rightReference", context.serialize(change.getRightReference()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ReferenceChange.class;
    }
}
