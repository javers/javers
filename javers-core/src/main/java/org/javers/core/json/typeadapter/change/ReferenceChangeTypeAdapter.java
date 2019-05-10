package org.javers.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.*;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

import static java.util.Optional.ofNullable;

class ReferenceChangeTypeAdapter extends ChangeTypeAdapter<ReferenceChange> {

    private static final String LEFT_REFERENCE_FIELD = "left";
    private static final String RIGHT_REFERENCE_FIELD = "right";

    public ReferenceChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ReferenceChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeStub stub = deserializeStub(jsonObject, context);

        GlobalId leftRef  = context.deserialize(jsonObject.get(LEFT_REFERENCE_FIELD),  GlobalId.class);
        GlobalId rightRef = context.deserialize(jsonObject.get(RIGHT_REFERENCE_FIELD), GlobalId.class);

        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);

        if (getChangeTypeField(jsonObject).equals("ReferenceChange")) {
            return new ReferenceChange(stub.id, stub.getPropertyName(), leftRef, rightRef, null, null, ofNullable(commitMetadata));
        }
        if (getChangeTypeField(jsonObject).equals("ReferenceAddedChange")) {
            return new ReferenceChange.ReferenceAddedChange(stub.id, stub.getPropertyName(), rightRef, null, ofNullable(commitMetadata));
        }
        if (getChangeTypeField(jsonObject).equals("ReferenceRemovedChange")) {
            return new ReferenceChange.ReferenceRemovedChange(stub.id, stub.getPropertyName(), leftRef, null, ofNullable(commitMetadata));
        }
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);

    }

    @Override
    public JsonElement toJson(ReferenceChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(LEFT_REFERENCE_FIELD,  context.serialize(change.getLeft()));
        jsonObject.add(RIGHT_REFERENCE_FIELD, context.serialize(change.getRight()));

        return jsonObject;
    }

    @Override
    public List<Class> getValueTypes() {
        return Lists.asList(ReferenceChange.class, ReferenceChange.ReferenceAddedChange.class, ReferenceChange.ReferenceRemovedChange.class);
    }
}
