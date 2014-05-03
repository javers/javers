package org.javers.core.json.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

public class ReferenceChangeTypeAdapter extends ChangeTypeAdapter<ReferenceChange> {

    private static final String LEFT_REFERENCE_FIELD = "leftReference";
    private static final String RIGHT_REFERENCE_FIELD = "rightReference";

    @Override
    public ReferenceChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeStub stub = deserializeStub(jsonObject, context);

        GlobalCdoId leftRef  = context.deserialize(jsonObject.get(LEFT_REFERENCE_FIELD),  GlobalCdoId.class);
        GlobalCdoId rightRef = context.deserialize(jsonObject.get(RIGHT_REFERENCE_FIELD), GlobalCdoId.class);

        return new ReferenceChange(stub.id, stub.property, leftRef, rightRef);
    }

    @Override
    public JsonElement toJson(ReferenceChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(LEFT_REFERENCE_FIELD,  context.serialize(change.getLeftReference()));
        jsonObject.add(RIGHT_REFERENCE_FIELD, context.serialize(change.getRightReference()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ReferenceChange.class;
    }
}
