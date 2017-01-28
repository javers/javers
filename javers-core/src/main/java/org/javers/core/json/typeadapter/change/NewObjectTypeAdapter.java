package org.javers.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Optional;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.metamodel.type.TypeMapper;

import static java.util.Optional.ofNullable;

class NewObjectTypeAdapter extends ChangeTypeAdapter<NewObject> {

    public NewObjectTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public NewObject fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;

        CommitMetadata commitMetadata = deserializeCommitMetadata(jsonObject, context);
        return new NewObject(deserializeAffectedCdoId(jsonObject,context), Optional.empty(), ofNullable(commitMetadata));
    }

    @Override
    public Class getValueType() {
        return NewObject.class;
    }
}
