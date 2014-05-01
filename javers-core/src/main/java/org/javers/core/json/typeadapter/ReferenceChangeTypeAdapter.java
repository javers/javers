package org.javers.core.json.typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.changetype.ReferenceChange;

public class ReferenceChangeTypeAdapter extends AbstractTypeAdapter<ReferenceChange> {

    @Override
    public JsonElement toJson(ReferenceChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add("leftReference", globalCdoId(change.getLeftReference(), context));
        jsonObject.add("rightReference", globalCdoId(change.getRightReference(), context));

        return jsonObject;
    }

}
