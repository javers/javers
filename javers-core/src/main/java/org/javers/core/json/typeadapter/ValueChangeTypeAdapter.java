package org.javers.core.json.typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.changetype.ValueChange;

public class ValueChangeTypeAdapter extends AbstractTypeAdapter<ValueChange> {

    @Override
    public JsonElement toJson(ValueChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add("leftValue", context.serialize(change.getWrappedLeftValue()));
        jsonObject.add("rightValue", context.serialize(change.getWrappedRightValue()));

        return jsonObject;
    }
}
