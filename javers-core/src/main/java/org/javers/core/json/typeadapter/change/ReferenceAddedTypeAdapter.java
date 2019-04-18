package org.javers.core.json.typeadapter.change;

import static java.util.Optional.ofNullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ReferenceAddedChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.TypeMapper;

public class ReferenceAddedTypeAdapter extends ChangeTypeAdapter<ReferenceAddedChange> {

    private static final String RIGHT_REFERENCE_FIELD = "right";

    public ReferenceAddedTypeAdapter(final TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ReferenceAddedChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeStub stub = deserializeStub(jsonObject, context);

        GlobalId rightRef = context.deserialize(jsonObject.get(RIGHT_REFERENCE_FIELD), GlobalId.class);

        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);
        return new ReferenceAddedChange(stub.id, stub.getPropertyName(), ofNullable(commitMetadata), rightRef, null);
    }

    @Override
    public JsonElement toJson(ReferenceAddedChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(RIGHT_REFERENCE_FIELD, context.serialize(change.getRight()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ReferenceAddedChange.class;
    }
}
