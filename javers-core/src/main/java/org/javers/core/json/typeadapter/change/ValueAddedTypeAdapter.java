package org.javers.core.json.typeadapter.change;

import static java.util.Optional.ofNullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ValueAddedChange;
import org.javers.core.metamodel.type.TypeMapper;

public class ValueAddedTypeAdapter extends ChangeTypeAdapter<ValueAddedChange> {

    private static final String RIGHT_VALUE_FIELD = "right";

    public ValueAddedTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ValueAddedChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeStub stub = deserializeStub(jsonObject, context);

        Object rightValue = context.deserialize(jsonObject.get(RIGHT_VALUE_FIELD), stub.property.getGenericType());

        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);
        return new ValueAddedChange(stub.id, stub.getPropertyName(), ofNullable(commitMetadata), rightValue);
    }

    @Override
    public JsonElement toJson(ValueAddedChange change, JsonSerializationContext context) {
        JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(RIGHT_VALUE_FIELD, context.serialize(change.getRight()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return ValueAddedChange.class;
    }

}
