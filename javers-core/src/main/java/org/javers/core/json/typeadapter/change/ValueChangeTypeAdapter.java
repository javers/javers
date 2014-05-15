package org.javers.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.changetype.ValueChange;

public class ValueChangeTypeAdapter extends ChangeTypeAdapter<ValueChange> {

    @Override
    public ValueChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeStub stub = deserializeStub(jsonObject, context);

        Object leftValue  = context.deserialize(jsonObject.get("leftValue"),  stub.property.getType());
        Object rightValue = context.deserialize(jsonObject.get("rightValue"), stub.property.getType());

        return new ValueChange(stub.id, stub.property, leftValue, rightValue);
    }

    @Override
    public JsonElement toJson(ValueChange change, JsonSerializationContext context) {
        JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add("leftValue", context.serialize(change.getLeftValue()));
        jsonObject.add("rightValue", context.serialize(change.getRightValue()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ValueChange.class;
    }
}
