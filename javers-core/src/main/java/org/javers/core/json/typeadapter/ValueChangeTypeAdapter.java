package org.javers.core.json.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

public class ValueChangeTypeAdapter extends ChangeTypeAdapter<ValueChange> {

    @Override
    public ValueChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        GlobalCdoId id = deserializeAffectedCdoId(jsonObject,context);

        Property property = deserializeProperty(jsonObject, id);

        Object leftValue  = context.deserialize(jsonObject.get("leftValue"),  property.getType());
        Object rightValue = context.deserialize(jsonObject.get("rightValue"), property.getType());

        return new ValueChange(id, property, leftValue, rightValue);
    }

    @Override
    public JsonElement toJson(ValueChange change, JsonSerializationContext context) {
        JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add("leftValue", context.serialize(change.getWrappedLeftValue()));
        jsonObject.add("rightValue", context.serialize(change.getWrappedRightValue()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ValueChange.class;
    }
}
