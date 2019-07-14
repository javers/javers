package org.javers.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.PropertyChangeMetadata;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.TypeMapper;

class ReferenceChangeTypeAdapter extends ChangeTypeAdapter<ReferenceChange> {

    private static final String LEFT_REFERENCE_FIELD = "left";
    private static final String RIGHT_REFERENCE_FIELD = "right";

    public ReferenceChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ReferenceChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeMetadata stub = deserializeStub(jsonObject, context);

        GlobalId leftRef  = context.deserialize(jsonObject.get(LEFT_REFERENCE_FIELD),  GlobalId.class);
        GlobalId rightRef = context.deserialize(jsonObject.get(RIGHT_REFERENCE_FIELD), GlobalId.class);

        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);
        return new ReferenceChange(stub, leftRef, rightRef, null, null);
    }

    @Override
    public JsonElement toJson(ReferenceChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(LEFT_REFERENCE_FIELD,  context.serialize(change.getLeft()));
        jsonObject.add(RIGHT_REFERENCE_FIELD, context.serialize(change.getRight()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ReferenceChange.class;
    }
}
