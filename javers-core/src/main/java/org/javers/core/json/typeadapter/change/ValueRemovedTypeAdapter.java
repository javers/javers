package org.javers.core.json.typeadapter.change;

import static java.util.Optional.ofNullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.ValueRemovedChange;
import org.javers.core.metamodel.type.TypeMapper;

public class ValueRemovedTypeAdapter extends ChangeTypeAdapter<ValueRemovedChange> {

    private static final String LEFT_VALUE_FIELD = "left";

    public ValueRemovedTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ValueRemovedChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeStub stub = deserializeStub(jsonObject, context);

        Object leftValue  = context.deserialize(jsonObject.get(LEFT_VALUE_FIELD),  stub.property.getGenericType());

        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);
        return new ValueRemovedChange(stub.id, stub.getPropertyName(), ofNullable(commitMetadata), leftValue);
    }

    @Override
    public JsonElement toJson(ValueRemovedChange change, JsonSerializationContext context) {
        JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(LEFT_VALUE_FIELD, context.serialize(change.getLeft()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ValueRemovedChange.class;
    }

}
