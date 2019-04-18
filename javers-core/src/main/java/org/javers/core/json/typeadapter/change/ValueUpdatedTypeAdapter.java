package org.javers.core.json.typeadapter.change;

import static java.util.Optional.ofNullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.ValueUpdatedChange;
import org.javers.core.metamodel.type.TypeMapper;

public class ValueUpdatedTypeAdapter extends ChangeTypeAdapter<ValueUpdatedChange> {

    private static final String LEFT_VALUE_FIELD = "left";
    private static final String RIGHT_VALUE_FIELD = "right";

    public ValueUpdatedTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ValueUpdatedChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeStub stub = deserializeStub(jsonObject, context);

        Object leftValue  = context.deserialize(jsonObject.get(LEFT_VALUE_FIELD),  stub.property.getGenericType());
        Object rightValue = context.deserialize(jsonObject.get(RIGHT_VALUE_FIELD), stub.property.getGenericType());

        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);
        return new ValueUpdatedChange(stub.id, stub.getPropertyName(), leftValue, rightValue, ofNullable(commitMetadata));
    }

    @Override
    public JsonElement toJson(ValueUpdatedChange change, JsonSerializationContext context) {
        JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(LEFT_VALUE_FIELD, context.serialize(change.getLeft()));
        jsonObject.add(RIGHT_VALUE_FIELD, context.serialize(change.getRight()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ValueUpdatedChange.class;
    }
}
