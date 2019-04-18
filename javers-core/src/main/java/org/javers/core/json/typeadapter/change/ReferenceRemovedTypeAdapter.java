package org.javers.core.json.typeadapter.change;

import static java.util.Optional.ofNullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ReferenceRemovedChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.TypeMapper;

public class ReferenceRemovedTypeAdapter extends ChangeTypeAdapter<ReferenceRemovedChange> {

    public ReferenceRemovedTypeAdapter(final TypeMapper typeMapper) {
        super(typeMapper);
    }

    private static final String LEFT_REFERENCE_FIELD = "left";

    @Override
    public ReferenceRemovedChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeStub stub = deserializeStub(jsonObject, context);

        GlobalId leftRef = context
            .deserialize(jsonObject.get(LEFT_REFERENCE_FIELD), GlobalId.class);

        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);
        return new ReferenceRemovedChange(stub.id, stub.getPropertyName(),
            ofNullable(commitMetadata), leftRef, null);
    }

    @Override
    public JsonElement toJson(ReferenceRemovedChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(LEFT_REFERENCE_FIELD, context.serialize(change.getLeft()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ReferenceRemovedChange.class;
    }
}
