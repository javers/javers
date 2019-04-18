package org.javers.core.json.typeadapter.change;

import static java.util.Optional.ofNullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ReferenceUpdatedChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.TypeMapper;

public class ReferenceUpdatedTypeAdapter extends ChangeTypeAdapter<ReferenceUpdatedChange> {
    private static final String LEFT_REFERENCE_FIELD = "left";
    private static final String RIGHT_REFERENCE_FIELD = "right";

    public ReferenceUpdatedTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ReferenceUpdatedChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeStub stub = deserializeStub(jsonObject, context);

        GlobalId leftRef  = context.deserialize(jsonObject.get(LEFT_REFERENCE_FIELD),  GlobalId.class);
        GlobalId rightRef = context.deserialize(jsonObject.get(RIGHT_REFERENCE_FIELD), GlobalId.class);

        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);
        return new ReferenceUpdatedChange(stub.id, stub.getPropertyName(), leftRef, rightRef, null, null, ofNullable(commitMetadata));
    }

    @Override
    public JsonElement toJson(ReferenceUpdatedChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(LEFT_REFERENCE_FIELD,  context.serialize(change.getLeft()));
        jsonObject.add(RIGHT_REFERENCE_FIELD, context.serialize(change.getRight()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ReferenceUpdatedChange.class;
    }
}
